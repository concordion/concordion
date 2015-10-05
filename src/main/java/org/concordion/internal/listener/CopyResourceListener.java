package org.concordion.internal.listener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.concordion.api.CopyResource;
import org.concordion.api.CopyResource.InsertType;
import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.IOUtil;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * Adds custom CSS to the generated specifications
 * @author sumnera
 */
public class CopyResourceListener implements DocumentParsingListener {
	private boolean removeDefaultCSS = false;
	private List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
	public boolean removeDefaultCSS() {
		return this.removeDefaultCSS;
	}
	
	public CopyResourceListener(ConcordionExtender builder, Object fixture) {
		
		File root = getRootPath(fixture.getClass());
		List<Class<?>> classes = getClassHierarchyParentFirst(fixture.getClass());
		
		for (Class<?> class1 : classes) {
			if (class1.isAnnotationPresent(CopyResource.class)) {
	            CopyResource annotation = class1.getAnnotation(CopyResource.class);
	            
	            if (annotation.removeDefaultCSS()) {
    				removeDefaultCSS = true;
    			}
    	
	            sourceFiles.addAll(getResourcesToAdd(class1, annotation, root));
	        }
        }
		
		for (ResourceToCopy source : sourceFiles) {
			if (source.fileName.endsWith(".css")) {
				if (source.insertType == InsertType.EMBEDDED) {
					builder.withEmbeddedCSS(IOUtil.readResourceAsString(source.getResourceName()));
				} else {
					builder.withLinkedCSS(source.getResourceName(), new Resource(source.getResourceName()));
				}
			} else if (source.fileName.endsWith(".js")) {
				if (source.insertType == InsertType.EMBEDDED) {
					builder.withEmbeddedJavaScript(IOUtil.readResourceAsString(source.getResourceName()));
				} else {
					builder.withLinkedJavaScript(source.getResourceName(), new Resource(source.getResourceName()));
				}
			} else {
				builder.withResource(source.getResourceName(), new Resource(source.getResourceName()));
			}
		}
	}
    
	private Collection<? extends ResourceToCopy> getResourcesToAdd(Class<?> class1, CopyResource annotation, File root) {
		List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
		for (String sourceFile : annotation.sourceFiles()) {
			File search = (sourceFile.startsWith("/")) ? new File(root, sourceFile) : new File(getClassPath(class1), sourceFile);
			
			DirectoryStream<Path> dirStream;
			try {
				dirStream = Files.newDirectoryStream(Paths.get(search.getParent()), search.getName());
			} catch (IOException e) {
				throw new RuntimeException(String.format("Unable to search '%s' for '%s'", search.getParent(), search.getName()), e);
			}
			
			boolean found = false;
			
			for (Path path : dirStream) {
				found = true;
				String fileName = path.toString();
				
				if (fileName.startsWith(root.getPath())) {
					fileName = fileName.substring(root.getPath().length());
				}
				
				sourceFiles.add(new ResourceToCopy(fileName, annotation.insertType()));
			}
			
			if (!found) {
				throw new RuntimeException(String.format("No file found in '%s' matching '%s'", search.getParent(), search.getName()));
			}
		}
		
		return sourceFiles;
	}

	private File getRootPath(Class<?> class1) {
		try {
			return new File(class1.getClassLoader().getResource("").toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to get root path", e);
		}
	}
	
	private File getClassPath(Class<?> class1) {
		try {
			return new File(class1.getResource("").toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to get class path", e);
		}
	}
	 

	/**
     * Returns the specified class and all of its superclasses, excluding java.lang.Object,
     * ordered from the most super class to the specified class.
     */
    private List<Class<?>> getClassHierarchyParentFirst(Class<?> class1) {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> current = class1;
        while (current != null && current != Object.class) {
            classes.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(classes);
        return classes;
    }
    


	@Override
	public void beforeParsing(Document document) {
		Element head = document.getRootElement().getFirstChildElement("head");
		
		removeExistingStyling(head);
	}
	
	private void removeExistingStyling(Element head) {
		Elements links = head.getChildElements("link");
				
		for (int i = links.size() - 1; i >= 0; i--) {
			Element link = links.get(i);
		
			String href = link.getAttributeValue("href");
			
			if (href == null) {
				continue;
			}
			
			href = href.toLowerCase();
			
			// Remove any links to concordion.css created by developers
			if (href.contains("/concordion.css") || href.equals("concordion.css")) {
				head.removeChild(link);
				continue;
			}
			
			// Remove any links to custom css created by developers
			// TODO: Do we need to be able to turn this off?  There is a possibility that will delete something already added
			for (ResourceToCopy source : sourceFiles) {
				if (source.fileName.endsWith(".css") || source.fileName.endsWith(".js")) {
					if (href.contains("/" + source.getName().toLowerCase()) || href.equals(source.getName().toLowerCase())) {
						head.removeChild(link);
						break;
					}
				}
			}
		}
    }
	
	protected class ResourceToCopy {
		protected String fileName;
		protected InsertType insertType;
		
		public ResourceToCopy(String sourceFile, InsertType insertType) {
			this.fileName = sourceFile;
			this.insertType = insertType;
			
			if (fileName.startsWith(String.valueOf(File.separatorChar))) {
				fileName = fileName.substring(1);
			}				
			
			fileName = fileName.replace("\\", "/");
		}
		
		public String getResourceName() {
			return "/" + fileName;
		}
		
		public String getName() {
			return new File(fileName).getName();
		}
	}
}


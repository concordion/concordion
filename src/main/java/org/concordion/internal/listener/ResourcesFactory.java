package org.concordion.internal.listener;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.concordion.api.Resources;
import org.concordion.api.Resources.InsertType;
import org.concordion.api.Fixture;
import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.internal.util.IOUtil;

/**
 * Copy resources to the destination when specification is built.  Resources are copied to the same relative path as they are in the project.
 * Currently only supports resources on the file path.  Support for resources in Jars may come at a later date but will require java 1.7.
 * 
 * @author sumnera
 */
public class ResourcesFactory {
	private boolean includeDefaultStyling = true;
	private List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
	public boolean includeDefaultStyling() {
		return this.includeDefaultStyling;
	}
	
	public List<ResourceToCopy> getFiles() {
		return this.sourceFiles;
	}
	
	public ResourcesFactory(ConcordionExtender builder, Fixture fixture) {
		
		File root = getRootPath(fixture.getFixtureClass());
		List<Class<?>> classes = getClassHierarchyParentFirst(fixture.getFixtureClass());
		
		for (Class<?> class1 : classes) {
			if (class1.isAnnotationPresent(Resources.class)) {
	            Resources annotation = class1.getAnnotation(Resources.class);
	            
	            if (!annotation.includeDefaultStyling()) {
    				includeDefaultStyling = false;
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
	
	private Collection<? extends ResourceToCopy> getResourcesToAdd(Class<?> class1, Resources annotation, File root) {
		List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
		for (String sourceFile : annotation.value()) {
			File search = (sourceFile.startsWith("/")) ? new File(root, sourceFile) : new File(getClassPath(class1), sourceFile);
			
			String[] files = new File(search.getParent()).list(new WildcardFilter(search));
			
			boolean found = false;
			
			for (String file : files) {
				found = true;
				
				String fileName = new File(search.getParent(), file).getPath();
				
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
    
    protected class WildcardFilter implements FilenameFilter {
    	Pattern r;
    	
    	public WildcardFilter(File search) {
    		r = Pattern.compile(createRegexFromGlob(search.getName()));
    	}
    	
    	@Override
    	public boolean accept(File dir, String name) {
    		return r.matcher(name).matches();
    	}
    	
    	private String createRegexFromGlob(String glob)
    	{
    	    StringBuilder sb = new StringBuilder();
    	    
    	    for(int i = 0; i < glob.length(); ++i) {
    	        final char c = glob.charAt(i);
    	        
    	        switch(c) {
    		        case '*': sb.append(".*"); break;
    		        case '?': sb.append('.'); break;
    		        case '.': sb.append("\\."); break;
    		        case '\\': sb.append("\\\\"); break;
    		        default: sb.append(c);
    	        }
    	    }
    	    
    	    return sb.toString();
    	}
    }
}


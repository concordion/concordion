package org.concordion.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.concordion.api.Resources;
import org.concordion.api.Resources.InsertType;
import org.concordion.api.Fixture;

/**
 * Find resources to be copied to the destination when specification is built. 
 * Currently only supports resources on the file path.  Support for resources in Jars may come at a later date but will require java 1.7.
 * 
 * @author sumnera
 */
public class ResourceFinder {
	private Fixture fixture;
	private boolean includeDefaultStyling = true;
		
	public boolean includeDefaultStyling() {
		return this.includeDefaultStyling;
	}
	
	public ResourceFinder(Fixture fixture) {
		this.fixture = fixture;
	}
		
	public List<ResourceToCopy> getResourcesToCopy() {
		List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
		List<File> rootPaths = getRootPaths(fixture.getFixtureClass());
		List<Class<?>> classes = getClassHierarchyParentFirst(fixture.getFixtureClass());
		
		for (Class<?> class1 : classes) {
			if (class1.isAnnotationPresent(Resources.class)) {
	            Resources annotation = class1.getAnnotation(Resources.class);
	            
	            if (!annotation.includeDefaultStyling()) {
    				includeDefaultStyling = false;
    			}
    	
	            sourceFiles.addAll(getResourcesToAdd(class1, annotation, rootPaths));
	        }
        }
		
		return sourceFiles;
	}
	
	private Collection<? extends ResourceToCopy> getResourcesToAdd(Class<?> class1, Resources annotation, List<File> rootPaths) {
		List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();

		String packageName = getPackageName(class1);
		
		for (String sourceFile : annotation.value()) {
			boolean found = false;
			
			for (File root : rootPaths) {
				File searchPath = getAbsoluteSearchPath(root, packageName, sourceFile);

				String[] files = findMatchingFiles(searchPath);
				
				for (String file : files) {
					found = true;
					
					String fileName = new File(searchPath.getParent(), file).getPath();
					
					if (fileName.startsWith(root.getPath())) {
						fileName = fileName.substring(root.getPath().length());
					}
					
					sourceFiles.add(new ResourceToCopy(fileName, annotation.insertType()));
				}
			}
			
			if (!found) {
				StringBuilder msg = new StringBuilder();
				msg.append(String.format("No file found matching '%s' in:", sourceFile));
				
				for (File root : rootPaths) {
					msg.append("\r\n\t* ").append(root.getPath());
					
					if (!isSearchRoot(sourceFile)) {
						msg.append(File.separator).append(packageName);
					}
				}						
				
				throw new RuntimeException(msg.toString());
			}
		}
		
		return sourceFiles;
	}

    private String[] findMatchingFiles(File searchPath) {
        String[] files = new File(searchPath.getParent()).list(new WildcardFilter(searchPath));

        if (files == null) {
            files = new String[] {};
        }
        return files;
    }

    private File getAbsoluteSearchPath(File root, String packageName, String sourceFile) {
        File search;
        if (isSearchRoot(sourceFile)) {
            search = new File(root, sourceFile);
        } else {
            search = new File(root, packageName);
            search = new File(search, sourceFile);
        }
        return search;
    }

	private boolean isSearchRoot(String sourceFile) {
		return sourceFile.startsWith("/");
	}

    private String getPackageName(Class<?> class1) {
        String qualifiedClassName = class1.getName();
        int lastDot = qualifiedClassName.lastIndexOf(".");
        String packageName = "";
        if (lastDot != -1) {
            packageName = qualifiedClassName.substring(0, lastDot);
            packageName = packageName.replace('.', File.separatorChar);
        }
        return packageName;
    }

	private List<File> getRootPaths(Class<?> class1) {
		List<File> rootPaths = new ArrayList<File>();
		
		Enumeration<URL> resources;
		try {
			resources = class1.getClassLoader().getResources("");
		
			while (resources.hasMoreElements()) {
                rootPaths.add(new File(resources.nextElement().toURI()));
            }
		} catch (IOException e) {
			throw new RuntimeException("Unable to get root path", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to get root path", e);
		}
		
		return rootPaths;
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
    
    public class ResourceToCopy {
		protected String fileName;
		public InsertType insertType;
		
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
		
		public boolean isStyleSheet() {
			return fileName.endsWith(".css");
		}
		
		public boolean isScript() {
			return fileName.endsWith(".js");
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


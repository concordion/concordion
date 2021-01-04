package org.concordion.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.concordion.api.ConcordionResources;
import org.concordion.api.Fixture;
import org.concordion.internal.util.SimpleFormatter;

/**
 * Find resources to be copied to the destination when specification is built. 
 * Currently only supports resources on the file path.  Support for resources in Jars may come at a later date but will require java 1.7.
 * 
 * @author sumnera
 */
public class ResourceFinder {
    private FixtureType fixtureType;
    private boolean includeDefaultStyling = true;

    public boolean includeDefaultStyling() {
        return this.includeDefaultStyling;
    }

    public ResourceFinder(FixtureType fixtureType) {
        this.fixtureType = fixtureType;
    }

    public List<ResourceToCopy> getResourcesToCopy() {
        List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();

        List<File> rootPaths = fixtureType.getClassPathRoots();
        List<Class<?>> classes = fixtureType.getClassHierarchyParentFirst();

        for (Class<?> class1 : classes) {
            if (isAnnotationDeclared(class1, ConcordionResources.class)) {
                ConcordionResources annotation = class1.getAnnotation(ConcordionResources.class);

                if (!annotation.includeDefaultStyling()) {
                    includeDefaultStyling = false;
                }

                sourceFiles.addAll(getResourcesToAdd(class1, annotation, rootPaths));
            }
        }

        return sourceFiles;
    }

    private boolean isAnnotationDeclared(Class<?> class1, Class<?> annotationClass) {
        Annotation[] annotations = class1.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationClass.getName())) {
                return true;
            }
        }

        return false;
    }

    private Collection<? extends ResourceToCopy> getResourcesToAdd(Class<?> class1, ConcordionResources annotation, List<File> rootPaths) {
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
                msg.append(SimpleFormatter.format("No file found matching '%s' in:", sourceFile));

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
        return search.toPath().normalize().toFile();
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

    private static class WildcardFilter implements FilenameFilter {
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

package org.concordion.internal;

import org.concordion.api.*;
import org.concordion.api.option.ConcordionOptions;
import org.concordion.internal.util.SimpleFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FixtureType implements FixtureDeclarations {
    private static final Logger LOG = Logger.getLogger(FixtureType.class.getSimpleName());
    private Class<?> fixtureClass;
    private ArrayList<Class<?>> classHierarchyParentFirst;

    public FixtureType(Class<?> fixtureClass) {
        this.fixtureClass = fixtureClass;
    }

    @Override
    public boolean declaresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    @Override
    public boolean declaresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    @Override
    public boolean declaresResources() {
        return fixtureClass.isAnnotationPresent(ConcordionResources.class);
    }

    @Override
    public Class<? extends Throwable>[] getDeclaredFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
    }

    public boolean declaresStatus(ImplementationStatus status) {
        return fixtureClass.isAnnotationPresent(status.getAnnotation());
    }

    @Override
    public ImplementationStatus getDeclaredImplementationStatus() {
        for (ImplementationStatus status : ImplementationStatus.values()) {
            if (declaresStatus(status)) {
                return status;
            }
        }
    
        return ImplementationStatus.EXPECTED_TO_PASS;
    }

    /**
     * @return the fixture class and all of its superclasses, excluding java.lang.Object,
     * ordered from the most super class to the fixture class.
     */
    public List<Class<?>> getClassHierarchyParentFirst() {
        if (classHierarchyParentFirst != null) {
            return classHierarchyParentFirst;
        }
        
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> current = fixtureClass;
        while (current != null && current != Object.class) {
            classes.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(classes);
        this.classHierarchyParentFirst = classes;
        return classes;
    }

    @Override
    public List<ConcordionOptions> getDeclaredConcordionOptionsParentFirst() {
        List<ConcordionOptions> annotations = new ArrayList<ConcordionOptions>();
        for (Class<?> class1 : getClassHierarchyParentFirst()) {
            ConcordionOptions annotation = class1.getAnnotation(ConcordionOptions.class);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    public Class<?> getFixtureClass() {
        return fixtureClass;
    }

    public List<File> getClassPathRoots() {
    	List<File> rootPaths = new ArrayList<File>();

    	Enumeration<URL> resources;
    	try {
    		resources = getFixtureClass().getClassLoader().getResources("");

    		while (resources.hasMoreElements()) {
                URI uri = resources.nextElement().toURI();
                try {
                    rootPaths.add(new File(uri));
                } catch (IllegalArgumentException e) {
                  if (e.getMessage().equals("URI is not hierarchical")) {
                      LOG.log(Level.FINER, String.format("Skipping resource %s: Java 8 or > detected", uri), e);
                      continue;
                  }
                  throw e;
                }
            }
    	} catch (IOException e) {
    		throw new RuntimeException("Unable to get root path", e);
    	} catch (URISyntaxException e) {
    		throw new RuntimeException("Unable to get root path", e);
    	}

    	return rootPaths;
    }

    public String getDescription() {
        String name = removeSuffix(fixtureClass.getSimpleName());
        return SimpleFormatter.format("[Concordion Specification for '%s']", name); // Based on suggestion by Danny Guerrier
    }

    public String getFixturePathWithoutSuffix() {
        String slashedClassName = fixtureClass.getName().replaceAll("\\.", "/");
        return removeSuffix(slashedClassName);
    }

    private String removeSuffix(String fixtureName) {
        return FixtureSpecificationMapper.removeSuffixFromFixtureName(fixtureName);
    }
}

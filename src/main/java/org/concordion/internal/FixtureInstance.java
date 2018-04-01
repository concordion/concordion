package org.concordion.internal;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.option.ConcordionOptions;
import org.concordion.internal.scopedObjects.ScopedFieldStore;
import org.concordion.internal.util.SimpleFormatter;

public class FixtureInstance implements Fixture, FixtureDeclarations {
    private final Object fixtureObject;
    private final ScopedFieldStore scopedFieldStore;
    private final Class<? extends Object> fixtureClass;
    private final FixtureType fixtureType;

    public FixtureInstance(Object fixtureObject) {
        this.fixtureObject = fixtureObject;
        this.fixtureClass = fixtureObject.getClass();
        this.fixtureType = new FixtureType(fixtureClass);
        scopedFieldStore = new ScopedFieldStore(this);
    }
    
    @Override
    public String toString() {
        return fixtureClass.getName();
    }
    
    @Override
    public Object getFixtureObject() {
        return fixtureObject;
    }
    
    @Override
    public Class<?> getFixtureClass() {
        return fixtureClass;
    }
    
    @Override
    public String getSpecificationDescription() {
        String name = removeSuffix(fixtureClass.getSimpleName());
        return SimpleFormatter.format("[Concordion Specification for '%s']", name); // Based on suggestion by Danny Guerrier
    }

    @Override
    public String getFixturePathWithoutSuffix() {
        String slashedClassName = fixtureClass.getName().replaceAll("\\.", "/");
        return removeSuffix(slashedClassName);
    }

    private String removeSuffix(String fixtureName) {
        return FixtureSpecificationMapper.removeSuffixFromFixtureName(fixtureName);
    }

    @Override
    public List<File> getClassPathRoots() {
    	List<File> rootPaths = new ArrayList<File>();
    	
    	Enumeration<URL> resources;
    	try {
    		resources = fixtureClass.getClassLoader().getResources("");
    	
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

    @Override
    public void setupForRun(Object fixtureObject) {
        scopedFieldStore.loadValuesIntoFields(fixtureObject, Scope.SPECIFICATION);
    }
    
    @Override
    public void beforeSuite() {
        invokeMethods(BeforeSuite.class);
    }

    @Override
    public void afterSuite() {
        invokeMethods(AfterSuite.class);
    }

    @Override
    public void beforeSpecification() {
        scopedFieldStore.saveValueFromFields(fixtureObject, Scope.SPECIFICATION);
        invokeMethods(BeforeSpecification.class);
    }

    @Override
    public void afterSpecification() {
        invokeMethods(AfterSpecification.class);
        scopedFieldStore.destroyFields(fixtureObject, Scope.SPECIFICATION);
    }

    @Override
    public void beforeProcessExample(String exampleName) {
        
    }
    
    @Override
    public void beforeExample(final String exampleName) {
        invokeMethods(BeforeExample.class, new SingleParameterSupplier(BeforeExample.class, ExampleName.class, exampleName));
    }
    
    @Override
    public void afterExample(String exampleName) {
        invokeMethods(AfterExample.class, new SingleParameterSupplier(AfterExample.class, ExampleName.class, exampleName));
    }

    @Override
    public void afterProcessExample(String exampleName) {
        scopedFieldStore.destroyFields(fixtureObject, Scope.EXAMPLE);
    }
    
    @Override
    public List<Class<?>> getClassHierarchyParentFirst() {
        return fixtureType.getClassHierarchyParentFirst();
    }
    
    private void invokeMethods(Class<? extends Annotation> methodAnnotation) {
        invokeMethods(methodAnnotation, null);
    }
    
    private void invokeMethods(Class<? extends Annotation> methodAnnotation, ParameterSupplier parameterSupplier) {
        for (Class<?> clazz : getClassHierarchyParentFirst()) {
            Method[] methods = clazz.getDeclaredMethods();
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(methodAnnotation)) {
                    try {
                        method.setAccessible(true);
                        Class<?>[] parameters = method.getParameterTypes();
                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                        Object[] paramValues = new Object[parameters.length];
                        if (parameters.length > 0) {
                            if (parameterSupplier == null) {
                                throw new AnnotationFormatError("Error invoking " + method + ". Methods annotated with '" + methodAnnotation + "' are not allowed parameters");
                            }
                            for (int i = 0; i < parameters.length; i++) {
                                paramValues[i] = parameterSupplier.getValueForParameter(method, parameters[i], parameterAnnotations[i]);
                            }
                        }
                        
                        method.invoke(fixtureObject, paramValues);
                    } catch (IllegalAccessException e) {
                        throw new AnnotationFormatError("Invalid permissions to invoke method: " + method.getName());
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof RuntimeException) {
                            throw (RuntimeException)e.getCause();
                        }
                        throw new RuntimeException(e.getCause());
                    }
                }
            }
        }        
    }

    @Override
    public boolean declaresFullOGNL() {
        return fixtureType.declaresFullOGNL();
    }

    @Override
    public boolean declaresFailFast() {
        return fixtureType.declaresFailFast();
    }

    @Override
    public Class<? extends Throwable>[] getDeclaredFailFastExceptions() {
        return fixtureType.getDeclaredFailFastExceptions();
    }

    @Override
    public boolean declaresResources() {
        return fixtureType.declaresResources();
    }

    @Override
    public ImplementationStatus getDeclaredImplementationStatus() {
        return fixtureType.getDeclaredImplementationStatus();
    }

    @Override
    public List<ConcordionOptions> getDeclaredConcordionOptionsParentFirst() {
        return fixtureType.getDeclaredConcordionOptionsParentFirst();
    }
}

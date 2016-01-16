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
import org.concordion.internal.scopedObjects.ScopedFieldStore;

public class FixtureInstance extends FixtureType implements Fixture, FixtureDeclarations {
    private final Object fixtureObject;
    private final ScopedFieldStore scopedFieldStore;

    public FixtureInstance(Object fixtureObject) {
        super(fixtureObject.getClass());
        this.fixtureObject = fixtureObject;
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
        return String.format("[Concordion Specification for '%s']", name); // Based on suggestion by Danny Guerrier
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

    private void invokeMethods(Class<? extends Annotation> annotation, Object... params) {
        invokeMethods(fixtureClass, annotation, params);
    }

    private void invokeMethods(Class<? extends Object> clazz, Class<? extends Annotation> annotation, Object... params) throws AnnotationFormatError {
        
        if (clazz == Object.class) {
            return;
        }
        
        invokeMethods(clazz.getSuperclass(), annotation, params);
        
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                try {
//TODO check method signature - before and after example methods can take String parameter with example name       
                    method.setAccessible(true);
                    method.invoke(fixtureObject, params);
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

    @Override
    public void setupForRun(Object fixtureObject) {
        scopedFieldStore.loadValuesIntoFields(fixtureObject, Scope.SUITE);
        scopedFieldStore.loadValuesIntoFields(fixtureObject, Scope.SPECIFICATION);
    }
    
    @Override
    public void beforeSuite() {
        scopedFieldStore.saveValueFromFields(fixtureObject, Scope.SUITE);
        invokeMethods(BeforeSuite.class);
    }

    @Override
    public void afterSuite() {
        invokeMethods(AfterSuite.class);
        scopedFieldStore.destroyFields(fixtureObject, Scope.SUITE);
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
    public void beforeExample(String exampleName) {
        invokeMethods(BeforeExample.class, exampleName);
    }
    
    @Override
    public void afterExample(String exampleName) {
        invokeMethods(AfterExample.class, exampleName);
        scopedFieldStore.destroyFields(fixtureObject, Scope.EXAMPLE);
    }
    
    @Override
    public List<Class<?>> getClassHierarchyParentFirst() {
        return super.getClassHierarchyParentFirst();
    }
}

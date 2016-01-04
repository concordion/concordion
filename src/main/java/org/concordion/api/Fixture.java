package org.concordion.api;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.concordion.internal.ConcordionFieldScope;
import org.concordion.internal.ConcordionScopeDeclaration;
import org.concordion.internal.scopedObjects.ConcordionScopedFieldImpl;
import org.concordion.internal.scopedObjects.ConcordionScopedObject;
import org.concordion.internal.scopedObjects.ConcordionScopedObjectFactory;
import org.concordion.internal.util.Check;

public class Fixture {
    private final Object fixtureObject;
    private Class<?> fixtureClass;

    private List<ConcordionScopedField> scopedFields = new ArrayList<ConcordionScopedField>();

    public Fixture(Object fixtureObject) {
        Check.notNull(fixtureObject, "Fixture is null");
        this.fixtureObject = fixtureObject;
        this.fixtureClass = fixtureObject.getClass();

        addScopedFields(scopedFields);
    }

    /**
     *
     * This method is called during object construction to configure all the scoped fields. The default behaviour is to
     * scan the fixture class for any scoping annotations. Protection is "protected" so subclasses can overwrite as necessary.
     *
     * @param scopedFields
     */
    protected void addScopedFields(List<ConcordionScopedField> scopedFields) {

        addScopedFields(fixtureClass, scopedFields);
    }

    private void addScopedFields(Class<?> klass, List<ConcordionScopedField> scopedFields) {

        if (klass == Object.class) {
            return;
        }

        addScopedFields(klass.getSuperclass(), scopedFields);

        // cycle through all the fields and add any annotated fields into the cache
        Field[] fields = klass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                // we only copy from the field when we are initialising the class
                ConcordionScopedObject<Object> scopedObject = createScopedObject(fixtureObject, field);

                if (scopedObject != null) {
                    // we only replace existing values when we are not initialising

                    scopedFields.add(new ConcordionScopedFieldImpl(scopedObject, field));
                }
            }
        }
    }

    private ConcordionScopedObject<Object> createScopedObject(Object fixtureObject, Field field) {

        String name = "";
        ConcordionFieldScope concordionFieldScope = null;

        // go through all the annotations on the field
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            // see if the annotation has a scope annotation
            ConcordionFieldScope annotationConcordionFieldScope = getScopeFromAnnotation(annotation);

            // double check there is only one annotated annotation.
            if (annotationConcordionFieldScope != null) {
                if (concordionFieldScope != null) {
                    throw new AnnotationFormatError("Multiple concordion scope annotations on field '" + field.getName() + "'");
                }
                concordionFieldScope = annotationConcordionFieldScope;

                // get the field name. Defaults to "" if not set - then we overwrite with the field name below if necessary
                try {
                    name = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
                } catch (Exception e) {
                    throw new AnnotationFormatError("Expected concordion scope annotation on field '"  + field.getName() + "' to also have a 'value()' method");
                }
            }
        }

        // did we find one?
        if (concordionFieldScope == null) {
            return null;
        }

        // use the field name if not set.
        if ("".equals(name)) {
            name = field.getName();
        }

        ConcordionScopedObject<Object> concordionScopedObject = createScopedObject(fixtureObject, field, name, concordionFieldScope);

        return concordionScopedObject;
    }

    /**
     *
     * Creates the scoped object for use in setting and getting the data from the fields. Protected so that a subclass can
     * override if necessary
     *
     * @param fixtureObject
     * @param field
     * @param name
     * @param concordionFieldScope
     * @return
     */
    protected ConcordionScopedObject<Object> createScopedObject(Object fixtureObject, Field field, String name, ConcordionFieldScope concordionFieldScope) {
        return ConcordionScopedObjectFactory.SINGLETON.create(fixtureObject.getClass(),
                name,
                field.getType(),
                concordionFieldScope);
    }

    private ConcordionFieldScope getScopeFromAnnotation(Annotation annotation) {
        ConcordionScopeDeclaration scopeDeclaration = annotation.annotationType().getAnnotation(ConcordionScopeDeclaration.class);
        ConcordionFieldScope concordionFieldScope = null;
        if (scopeDeclaration != null) {
            concordionFieldScope = scopeDeclaration.scope();
        }
        return concordionFieldScope;
    }


    public String getClassName() {
        return fixtureClass.getName();
    }
    
    public Object getFixtureObject() {
        return fixtureObject;
    }
    
    public Class<? extends Object> getFixtureClass() {
        return fixtureClass;
    }
    
    public boolean declaresStatus(ImplementationStatus status) {
        return fixtureClass.isAnnotationPresent(status.getAnnotation());
    }
    
    public boolean declaresFullOGNL() {
        return fixtureClass.isAnnotationPresent(FullOGNL.class);
    }

    public boolean declaresFailFast() {
        return fixtureClass.isAnnotationPresent(FailFast.class);
    }

    public boolean declaresResources() {
        return fixtureClass.isAnnotationPresent(ConcordionResources.class);
    }
    
    public Class<? extends Throwable>[] getFailFastExceptions() {
        FailFast failFastAnnotation = fixtureClass.getAnnotation(FailFast.class);
        Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
        return failFastExceptions;
    }

    public String getDescription() {
        String name = removeSuffix(fixtureClass.getSimpleName());
        return String.format("[Concordion Specification for '%s']", name); // Based on suggestion by Danny Guerrier
    }

    public String getFixturePathWithoutSuffix() {
        String slashedClassName = getClassName().replaceAll("\\.", "/");
        return removeSuffix(slashedClassName);
    }

    private String removeSuffix(String fixtureName) {
        return fixtureName.replaceAll("(Fixture|Test)$", "");
    }

    public ImplementationStatus getImplementationStatus() {
        for (ImplementationStatus status : ImplementationStatus.values()) {
            if (declaresStatus(status)) {
                return status;
            }
        }
    
        return ImplementationStatus.EXPECTED_TO_PASS;
    }

    /**
     * Returns the fixture class and all of its superclasses, excluding java.lang.Object,
     * ordered from the most super class to the fixture class.
     */
    public List<Class<?>> getClassHierarchyParentFirst() {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> current = getFixtureClass();
        while (current != null && current != Object.class) {
            classes.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(classes);
        return classes;
    }

    public List<File> getClassPathRoots() {
    	List<File> rootPaths = new ArrayList<File>();
    	
    	Enumeration<URL> resources;
    	try {
    		resources = getFixtureClass().getClassLoader().getResources("");
    	
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

    private void invokeMethods(Class<? extends Annotation> annotation) {
        invokeMethods(getFixtureClass(), annotation);
    }

    private void invokeMethods(Class<? extends Object> clazz, Class<? extends Annotation> annotation) throws AnnotationFormatError {
        
        if (clazz == Object.class) {
            return;
        }
        
        invokeMethods(clazz.getSuperclass(), annotation);
        
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                try {
                    method.setAccessible(true);
                    method.invoke(getFixtureObject(), new Object[]{});
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

    public void beforeSpecification() {
        for (ConcordionScopedField scopedField : scopedFields) {
            scopedField.copyValueIntoField(fixtureObject, false);
        }

        invokeMethods(BeforeSpecification.class);

        for (ConcordionScopedField scopedField : scopedFields) {
            scopedField.copyValueFromField(fixtureObject);
        }

    }

    public void afterSpecification() {
        invokeMethods(AfterSpecification.class);
    }

    public void setupForRun(Object fixtureObject) {
        for (ConcordionScopedField scopedField : scopedFields) {
            scopedField.copyValueIntoField(fixtureObject, true);
        }
    }

    public void beforeSuite() {
        invokeMethods(BeforeSuite.class);
    }

    public void afterSuite() {
        invokeMethods(AfterSuite.class);
    }
}

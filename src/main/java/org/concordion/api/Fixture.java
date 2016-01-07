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

import org.concordion.internal.FieldScope;
import org.concordion.internal.scopedObjects.ScopedField;
import org.concordion.internal.scopedObjects.ScopedFieldImpl;
import org.concordion.internal.scopedObjects.ScopedObject;
import org.concordion.internal.scopedObjects.ScopedObjectFactory;
import org.concordion.internal.util.Check;

public class Fixture {
    private final Object fixtureObject;
    private Class<?> fixtureClass;

    private List<ScopedField> scopedFields = new ArrayList<ScopedField>();

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
    protected void addScopedFields(List<ScopedField> scopedFields) {

        addScopedFields(fixtureClass, scopedFields);
    }

    private void addScopedFields(Class<?> klass, List<ScopedField> scopedFields) {

        if (klass == Object.class) {
            return;
        }

        addScopedFields(klass.getSuperclass(), scopedFields);

        Field[] fields = klass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                if (SpecificationScoped.class.isAssignableFrom(field.getType())) {
                    // we only copy from the field when we are initialising the class
                    ScopedObject scopedObject = createScopedObject(fixtureObject, field);
    
                    if (scopedObject != null) {
                        // we only replace existing values when we are not initialising
    
                        scopedFields.add(new ScopedFieldImpl(scopedObject, field));
                    }
                }
            }
        }
    }

    private ScopedObject createScopedObject(Object fixtureObject, Field field) {

        String name = field.getName();
        FieldScope concordionFieldScope = FieldScope.SPECIFICATION;
        return createScopedObject(fixtureObject, name, concordionFieldScope);
    }

    /**
     *
     * Creates the scoped object for use in setting and getting the data from the fields. Protected so that a subclass can
     * override if necessary
     *
     * @param fixtureObject
     * @param fieldName
     * @param fieldScope
     * @return
     */
    protected ScopedObject createScopedObject(Object fixtureObject, String fieldName, FieldScope fieldScope) {
        return ScopedObjectFactory.SINGLETON.create(fixtureObject.getClass(), fieldName, fieldScope);
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
        invokeMethods(BeforeSpecification.class);

        for (ScopedField scopedField : scopedFields) {
            scopedField.copyValueFromField(fixtureObject);
        }
    }

    public void afterSpecification() {
        invokeMethods(AfterSpecification.class);
        for (ScopedField scopedField : scopedFields) {
            scopedField.destroy(fixtureObject);
        }
    }

    public void setupForRun(Object fixtureObject) {
        for (ScopedField scopedField : scopedFields) {
            scopedField.copyValueIntoField(fixtureObject);
        }
    }

    public void beforeSuite() {
        invokeMethods(BeforeSuite.class);
    }

    public void afterSuite() {
        invokeMethods(AfterSuite.class);
    }
}

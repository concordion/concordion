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
import java.util.*;

import org.concordion.api.extension.Extension;
import org.concordion.internal.ScopeType;
import org.concordion.internal.ScopeDeclaration;
import org.concordion.internal.scopedObjects.ScopedField;
import org.concordion.internal.scopedObjects.ScopedFieldImpl;
import org.concordion.internal.scopedObjects.ScopedObject;
import org.concordion.internal.scopedObjects.ScopedObjectFactory;
import org.concordion.internal.util.Check;

public class Fixture {
    private static final ScopeType EXTENSION_SCOPE = ScopeType.SPECIFICATION;
    private final Object fixtureObject;
    private Class<?> fixtureClass;

    private Map<ScopeType, List<ScopedField>> scopedFields = new HashMap<ScopeType, List<ScopedField>>();

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
    protected void addScopedFields(Map<ScopeType, List<ScopedField>> scopedFields) {

        for (ScopeType scopeType : ScopeType.values()) {
            scopedFields.put(scopeType, new ArrayList<ScopedField>());
        }
        addScopedFields(fixtureClass, scopedFields);
    }

    private void addScopedFields(Class<?> klass, Map<ScopeType, List<ScopedField>> scopedFields) {

        if (klass == Object.class) {
            return;
        }

        addScopedFields(klass.getSuperclass(), scopedFields);

        Field[] fields = klass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                if (Scoped.class.isAssignableFrom(field.getType())) {
                    createScopedObject(fixtureObject, field, scopedFields);
                }
                if (field.getAnnotation(Extension.class) != null) {
                    createScopedExtension(fixtureObject, field, scopedFields);
                }
            }
        }
    }

    private void createScopedExtension(Object fixtureObject, Field field, Map<ScopeType, List<ScopedField>> scopedFields) {
        ScopedObject scopedObject = createScopedObject(fixtureObject, field.getName(), EXTENSION_SCOPE);
        scopedFields.get(EXTENSION_SCOPE).add(new ScopedFieldImpl(scopedObject, field));        
    }

    private void createScopedObject(Object fixtureObject, Field field, Map<ScopeType, List<ScopedField>> scopedFields) {

        String name = field.getName();
        ScopeType fieldScope = null;

        // go through all the annotations on the field
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            // see if the annotation has a scope annotation
            ScopeType annotationFieldScope = getScopeFromAnnotation(annotation);

            // double check there is only one annotated annotation.
            if (annotationFieldScope != null) {
                if (fieldScope != null) {
                    throw new AnnotationFormatError("Multiple field scope annotations on field '" + field.getName() + "'");
                }
                fieldScope = annotationFieldScope;

                // get the field name. Defaults to "" if not set - then we overwrite with the field name below if necessary
                try {
                    name = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
                } catch (Exception e) {
                    throw new AnnotationFormatError("Expected field scope annotation on field '"  + field.getName() + "' to also have a 'value()' method");
                }
            }
        }

        // did we find one?
        if (fieldScope == null) {
            return;
        }
        
        ScopedObject scopedObject = createScopedObject(fixtureObject, name, fieldScope);
        scopedFields.get(fieldScope).add(new ScopedFieldImpl(scopedObject, field));
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
    protected ScopedObject createScopedObject(Object fixtureObject, String fieldName, ScopeType fieldScope) {
        return ScopedObjectFactory.SINGLETON.create(fixtureObject.getClass(), fieldName, fieldScope);
    }
    
    private ScopeType getScopeFromAnnotation(Annotation annotation) {
        ScopeDeclaration scopeDeclaration = annotation.annotationType().getAnnotation(ScopeDeclaration.class);
        ScopeType fieldScope = null;
        if (scopeDeclaration != null) {
            fieldScope = scopeDeclaration.scope();
        }
        return fieldScope;
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

    private void invokeMethods(Class<? extends Annotation> annotation, Object... params) {
        invokeMethods(getFixtureClass(), annotation, params);
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
                    method.invoke(getFixtureObject(), params);
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

    public void beforeSuite() {
        invokeMethods(BeforeSuite.class);
    }

    public void afterSuite() {
        invokeMethods(AfterSuite.class);
    }

    public void beforeSpecification() {
        invokeMethods(BeforeSpecification.class);

        for (ScopedField scopedField : scopedFields.get(ScopeType.SPECIFICATION)) {
            scopedField.copyValueFromField(fixtureObject);
        }
    }

    public void afterSpecification() {
        invokeMethods(AfterSpecification.class);
        
        for (ScopedField scopedField : scopedFields.get(ScopeType.SPECIFICATION)) {
            scopedField.destroy(fixtureObject);
        }
    }

    public void setupForRun(Object fixtureObject) {
        for (ScopedField scopedField : scopedFields.get(ScopeType.SPECIFICATION)) {
            scopedField.copyValueIntoField(fixtureObject);
        }
    }

    public void beforeExample(String exampleName) {
        invokeMethods(BeforeExample.class, exampleName);
    }
    
    public void afterExample(String exampleName) {
        invokeMethods(AfterExample.class, exampleName);
        
        for (ScopedField scopedField : scopedFields.get(ScopeType.EXAMPLE)) {
            scopedField.destroy(fixtureObject);
        }
    }
}

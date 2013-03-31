package org.concordion.internal.extension;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.api.extension.Extension;
import org.concordion.api.extension.Extensions;
import org.concordion.internal.ConcordionBuilder;

public class FixtureExtensionLoader {
    
    public void addExtensions(final Object fixture, ConcordionBuilder concordionBuilder) {
        for (ConcordionExtension concordionExtension : getExtensionsForFixture(fixture)) {
            concordionExtension.addTo(concordionBuilder);
        }
    }

    public List<ConcordionExtension> getExtensionsForFixture(Object fixture) {
        final List<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();


        List<Class<?>> classes = getClassHierarchyParentFirst(fixture.getClass());
        for (Class<?> class1 : classes) {
            extensions.addAll(getExtensionsFromClassAnnotation(class1));
            extensions.addAll(getExtensionsFromAnnotatedFields(fixture, class1));
        }
        
        return extensions;
    }

    private Collection<? extends ConcordionExtension> getExtensionsFromClassAnnotation(Class<?> class1) {
        if (class1.isAnnotationPresent(Extensions.class)) {
            ArrayList<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();
            Extensions extensionsAnnotation = class1.getAnnotation(Extensions.class);
            Class<?>[] value = extensionsAnnotation.value();
            for (Class<?> class2 : value) {
                if (ConcordionExtension.class.isAssignableFrom(class2)) {
                    ConcordionExtension extension = (ConcordionExtension) newInstance(class2, "extension");
                    extensions.add(extension);
                } else if (ConcordionExtensionFactory.class.isAssignableFrom(class2)) {
                    ConcordionExtensionFactory factory = (ConcordionExtensionFactory) newInstance(class2, "extension factory");
                    extensions.add(factory.createExtension());
                } else {
                    throw new ExtensionInitialisationException(
                            String.format("Class %s specified in @Extensions annotation in class %s must implement %s or %s",
                            class2.getCanonicalName(), class1.getCanonicalName(),
                            ConcordionExtension.class.getCanonicalName(), ConcordionExtensionFactory.class.getCanonicalName()));
                }
                
            }
            return extensions;
        }
        return Collections.emptyList();
    }

    private Object newInstance(Class<?> type, String description) {
        Object object;
        try {
            object = type.newInstance();
        } catch (InstantiationException e) {
            throw new ExtensionInitialisationException(String.format("Unable to instantiate %s of class %s",
                    description, type.getCanonicalName()) , e);
        } catch (IllegalAccessException e) {
            throw new ExtensionInitialisationException(String.format("Unable to access no-args constructor of %s class %s",
                    description, type.getCanonicalName()) , e);
        }
        return object;
    }

    private List<ConcordionExtension> getExtensionsFromAnnotatedFields(Object fixture, Class<?> class1) {
        List<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();
        Field[] declaredFields = class1.getDeclaredFields();      
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Extension.class)) {
                validatePublic(field);
                ConcordionExtension extension = getExtensionField(fixture, field);
                validateNonNull(field, extension);
                extensions.add(extension);
            }
        }
        return extensions;
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
    
    private ConcordionExtension getExtensionField(Object fixture, Field field) {
        try {
            return (ConcordionExtension) field.get(fixture);
        } catch (ClassCastException e) {
            throw new ExtensionInitialisationException("Extension field '" + field.getName() + "' must implement org.concordion.api.extension.ConcordionExtension");
        } catch (IllegalArgumentException e) {
            throw new ExtensionInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        } catch (IllegalAccessException e) {
            throw new ExtensionInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        }
    }

    private void validatePublic(Field field) {
        if (!(Modifier.isPublic(field.getModifiers()))) {
            throw new ExtensionInitialisationException("Extension field '" + field.getName() + "' must be public");
        }
    }
    
    private void validateNonNull(Field field, ConcordionExtension extension) {
        if (extension == null) {
            throw new ExtensionInitialisationException("Extension field '" + field.getName() + "' must be non-null");
        }
    }
}
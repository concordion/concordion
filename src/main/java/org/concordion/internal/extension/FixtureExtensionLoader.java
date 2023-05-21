package org.concordion.internal.extension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.concordion.api.Fixture;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.api.extension.Extension;
import org.concordion.api.extension.Extensions;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.FixtureType;
import org.concordion.internal.util.SimpleFormatter;

public class FixtureExtensionLoader {

    public void addExtensions(Fixture fixture, ConcordionBuilder concordionBuilder) {
        for (ConcordionExtension concordionExtension : getExtensionsForFixture(fixture)) {
            concordionExtension.addTo(concordionBuilder);
        }
    }

    public void addExtensions(FixtureType fixtureType, ConcordionBuilder concordionBuilder) {
        for (ConcordionExtension concordionExtension : getExtensionsForFixtureType(fixtureType)) {
            concordionExtension.addTo(concordionBuilder);
        }
    }

    public List<ConcordionExtension> getExtensionsForFixture(Fixture fixture) {
        final List<ConcordionExtension> extensions = getExtensionsForFixtureType(fixture.getFixtureType());

        List<Class<?>> classes = fixture.getFixtureType().getClassHierarchyParentFirst();
        for (Class<?> class1 : classes) {
            extensions.addAll(getExtensionsFromAnnotatedFields(fixture, class1));
        }

        return extensions;
    }

    public List<ConcordionExtension> getExtensionsForFixtureType(FixtureType fixtureType) {
        final List<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();

        List<Class<?>> classes = fixtureType.getClassHierarchyParentFirst();
        for (Class<?> class1 : classes) {
            extensions.addAll(getExtensionsFromClassAnnotation(class1));
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
                            SimpleFormatter.format("Class %s specified in @Extensions annotation in class %s must implement %s or %s",
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
            throw new ExtensionInitialisationException(SimpleFormatter.format("Unable to instantiate %s of class %s",
                    description, type.getCanonicalName()) , e);
        } catch (IllegalAccessException e) {
            throw new ExtensionInitialisationException(SimpleFormatter.format("Unable to access no-args constructor of %s class %s",
                    description, type.getCanonicalName()) , e);
        }
        return object;
    }

    private List<ConcordionExtension> getExtensionsFromAnnotatedFields(Fixture fixture, Class<?> class1) {
        List<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();
        Field[] declaredFields = class1.getDeclaredFields();      
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Extension.class)) {
                field.setAccessible(true);
                ConcordionExtension extension = getExtensionField(fixture, field);
                validateNonNull(field, extension);
                extensions.add(extension);
            }
        }
        return extensions;
    }

    private ConcordionExtension getExtensionField(Fixture fixture, Field field) {
        try {
            return (ConcordionExtension) field.get(fixture.getFixtureObject());
        } catch (ClassCastException e) {
            throw new ExtensionInitialisationException("Extension field '" + field.getName() + "' must implement org.concordion.api.extension.ConcordionExtension");
        } catch (IllegalArgumentException e) {
            throw new ExtensionInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        } catch (IllegalAccessException e) {
            throw new ExtensionInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        }
    }

    private void validateNonNull(Field field, ConcordionExtension extension) {
        if (extension == null) {
            throw new ExtensionInitialisationException("Extension field '" + field.getName() + "' must be non-null");
        }
    }
}
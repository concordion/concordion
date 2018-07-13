package org.concordion.internal.scopedObjects;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.Fixture;
import org.concordion.api.Scope;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.extension.Extension;
import org.concordion.internal.FixtureType;
import org.concordion.internal.util.SimpleFormatter;

public class ScopedFieldStore {
    /**
     * Set extensions to have specification scope, since they are constructed once per specification and added to ConcordionBuilder.
     * This may change in the future to allow scope to be defined, but will require changes to the way extensions are instantiated.
     */
    static final Scope DEFAULT_EXTENSION_SCOPE = Scope.SPECIFICATION;
    private Map<Scope, List<ScopedField>> scopedFields = new HashMap<Scope, List<ScopedField>>();

    /**
     * This method is called during object construction to configure all the scoped fields. The default behaviour is to
     * scan the fixture class for any scoping annotations. Protection is "protected" so subclasses can overwrite as necessary.
     * @param fixture the fixture that the fields are being stored for
     */
    public ScopedFieldStore(Fixture fixture) {
        for (Scope scope : Scope.values()) {
            scopedFields.put(scope, new ArrayList<ScopedField>());
        }
        createScopedFields(fixture);
    }
    
    private void createScopedFields(Fixture fixture) {
        for (Class<?> clazz : fixture.getFixtureType().getClassHierarchyParentFirst()) {
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (field.getAnnotation(ConcordionScoped.class) != null) {
                        createScopedObjectField(fixture.getFixtureType(), field);
                    }
                    if (field.getAnnotation(Extension.class) != null) {
                        createScopedExtensionField(fixture.getFixtureType(), field);
                    }
                }
            }
        }
    }

    private void createScopedObjectField(Class<?> fixtureClass, Field field) {
        createScopedObjectField(fixtureClass, field);
    }

    private void createScopedObjectField(FixtureType fixtureType, Field field) {
        if (!field.getType().equals(ScopedObjectHolder.class)) {
            throw new AnnotationFormatError(SimpleFormatter.format("The '%s' annotation can only be applied to fields of type '%s'", ConcordionScoped.class.getSimpleName(), ScopedObjectHolder.class.getSimpleName()));
        }
    
        ConcordionScoped annotation = field.getAnnotation(ConcordionScoped.class);
        Scope fieldScope = annotation.value();
        String name = field.getName();
        ScopedObject scopedObject = createScopedObject(fixtureType, name, fieldScope);
        scopedFields.get(fieldScope).add(new ScopedFieldImpl(scopedObject, field));
    }

    private void createScopedExtensionField(Class<?> fixtureClass, Field field) {
        createScopedExtensionField(fixtureClass, field);
    }

    private void createScopedExtensionField(FixtureType fixtureType, Field field) {
        ScopedObject scopedObject = createScopedObject(fixtureType, field.getName(), DEFAULT_EXTENSION_SCOPE);
        scopedFields.get(DEFAULT_EXTENSION_SCOPE).add(new ScopedFieldImpl(scopedObject, field));        
    }

    /**
     * Creates the scoped object for use in setting and getting the data from the fields. Protected so that a subclass can
     * override if necessary.
     * @param fixtureClass fixture class
     * @param fieldName name of field in fixture class
     * @param fieldScope scope to be applied to the field
     * @return scoped object
     */
    protected ScopedObject createScopedObject(Class<?> fixtureClass, String fieldName, Scope fieldScope) {
        return createScopedObject(fixtureClass, fieldName, fieldScope);
    }

    /**
     * Creates the scoped object for use in setting and getting the data from the fields. Protected so that a subclass can
     * override if necessary.
     * @param fixtureType fixture class
     * @param fieldName name of field in fixture class
     * @param fieldScope scope to be applied to the field
     * @return scoped object
     */
    protected ScopedObject createScopedObject(FixtureType fixtureType, String fieldName, Scope fieldScope) {
        return ScopedObjectFactory.SINGLETON.create(fixtureType.getFixtureClass(), fieldName, fieldScope);
    }
    
    
    public void saveValueFromFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.saveValueFromField(fixtureObject);
        }
    }

    public void loadValuesIntoFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.loadValueIntoField(fixtureObject);
        }
    }
    
    public void destroyFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.destroy(fixtureObject);
        }
    }
}

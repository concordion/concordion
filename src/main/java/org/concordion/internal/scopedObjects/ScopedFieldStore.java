package org.concordion.internal.scopedObjects;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.Scope;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.extension.Extension;

public class ScopedFieldStore {
    static final Scope DEFAULT_EXTENSION_SCOPE = Scope.SPECIFICATION;
    private Map<Scope, List<ScopedField>> scopedFields = new HashMap<Scope, List<ScopedField>>();

    /**
     * This method is called during object construction to configure all the scoped fields. The default behaviour is to
     * scan the fixture class for any scoping annotations. Protection is "protected" so subclasses can overwrite as necessary.
     * @param fixtureClass the class that the fields are being stored for
     */
    public ScopedFieldStore(Class<?> fixtureClass) {
        for (Scope Scope : Scope.values()) {
            scopedFields.put(Scope, new ArrayList<ScopedField>());
        }
        addScopedFields(fixtureClass, fixtureClass);
    }
    
    private void addScopedFields(Class<?> fixtureClass, Class<?> currentClass) {
        if (currentClass == Object.class) {
            return;
        }
        
        addScopedFields(fixtureClass, currentClass.getSuperclass());
        
        Field[] fields = currentClass.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                if (field.getAnnotation(ConcordionScoped.class) != null) {
                    createScopedObjectField(fixtureClass, field);
                }
                if (field.getAnnotation(Extension.class) != null) {
                    createScopedExtensionField(fixtureClass, field);
                }
            }
        }
    }

    private void createScopedObjectField(Class<?> fixtureClass, Field field) {
        if (!field.getType().equals(ScopedObjectHolder.class)) {
            throw new AnnotationFormatError(String.format("The '%s' annotation can only be applied to fields of type '%s'", ConcordionScoped.class.getSimpleName(), ScopedObjectHolder.class.getSimpleName()));
        }
    
        ConcordionScoped annotation = field.getAnnotation(ConcordionScoped.class);
        Scope fieldScope = annotation.value();
        String name = field.getName();
        ScopedObject scopedObject = createScopedObject(fixtureClass, name, fieldScope);
        scopedFields.get(fieldScope).add(new ScopedFieldImpl(scopedObject, field));
    }

    private void createScopedExtensionField(Class<?> fixtureClass, Field field) {
        ScopedObject scopedObject = createScopedObject(fixtureClass, field.getName(), DEFAULT_EXTENSION_SCOPE);
        scopedFields.get(DEFAULT_EXTENSION_SCOPE).add(new ScopedFieldImpl(scopedObject, field));        
    }
    
    /**
     * Creates the scoped object for use in setting and getting the data from the fields. Protected so that a subclass can
     * override if necessary
     * @param fixtureClass 
     * @param fieldName
     * @param fieldScope
     * @return
     */
    protected ScopedObject createScopedObject(Class<?> fixtureClass, String fieldName, Scope fieldScope) {
        return ScopedObjectFactory.SINGLETON.create(fixtureClass, fieldName, fieldScope);
    }
    
    
    public void saveValueFromFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.copyValueFromField(fixtureObject);
        }
    }

    public void loadValuesIntoFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.copyValueIntoField(fixtureObject);
        }
    }
    
    public void destroyFields(Object fixtureObject, Scope scope) {
        for (ScopedField scopedField : scopedFields.get(scope)) {
            scopedField.destroy(fixtureObject);
        }
    }
}

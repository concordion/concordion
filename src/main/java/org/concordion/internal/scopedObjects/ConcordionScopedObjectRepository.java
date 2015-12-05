package org.concordion.internal.scopedObjects;

import org.concordion.internal.ConcordionScopedField;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tim on 3/12/15.
 */
public class ConcordionScopedObjectRepository {

    private Map<ScopedObjectRepositoryKey, ScopedObjectRepositoryValue> repo;
    private Object syncObject = new Object();

    public ConcordionScopedObjectRepository() {
        repo = new ConcurrentHashMap<ScopedObjectRepositoryKey, ScopedObjectRepositoryValue>();
    }

    public <T> T getObject(Class<? extends T> scopedObjectClass, String name, ConcordionScopedField.Scope scope, Class<?> specificationClass) throws InstantiationException, IllegalAccessException {

        ScopedObjectRepositoryKey<T> key = new ScopedObjectRepositoryKey<T>(scopedObjectClass, name, scope, specificationClass);

        // return a new one each time if the object is example scoped
        if (scope == ConcordionScopedField.Scope.EXAMPLE) {
            ScopedObjectRepositoryValue<T> value = createObject(key);
            return value.getObject();
        }

        // otherwise look up in the repo
        synchronized (syncObject) {

            ScopedObjectRepositoryValue<T> value = repo.get(key);

            // if not there, then create and put.
            if (value == null) {
                value = createObject(key);
                repo.put(key, value);
            }

            return value.getObject();
        }
    }

    private ScopedObjectRepositoryValue createObject(ScopedObjectRepositoryKey key) throws InstantiationException, IllegalAccessException {
        return new ScopedObjectRepositoryValue(key);
    }
}

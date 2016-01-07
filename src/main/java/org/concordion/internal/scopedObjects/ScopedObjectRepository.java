package org.concordion.internal.scopedObjects;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.internal.FieldScope;

/**
 * Created by tim on 3/12/15.
 */
public class ScopedObjectRepository {

    private Map<ScopedObjectRepositoryKey, Object> repo;

    public ScopedObjectRepository() {
        repo = new ConcurrentHashMap<ScopedObjectRepositoryKey, Object>();
    }

    public Object getObject(String name, FieldScope concordionFieldScope, Class<?> specificationClass) throws InstantiationException, IllegalAccessException {

        ScopedObjectRepositoryKey key = new ScopedObjectRepositoryKey(name, concordionFieldScope, specificationClass);

//        // return a new one each time if the object is example scoped
//        if (concordionFieldScope == ConcordionFieldScope.EXAMPLE) {
//            Object<T> value = createObject(key);
//            return value.getObject();
//        }

        return repo.get(key);
    }

    public <T> void setObject(String name, FieldScope concordionFieldScope, Class<?> specificationClass, T existingValue) {
        ScopedObjectRepositoryKey key = new ScopedObjectRepositoryKey(name, concordionFieldScope, specificationClass);

        repo.put(key, existingValue);
    }
    
    static class ScopedObjectRepositoryKey {

        private final String name;
        private final FieldScope concordionFieldScope;
        private final Class<?> specificationClass;

        public ScopedObjectRepositoryKey(String name, FieldScope concordionFieldScope, Class<?> specificationClass) {
            this.name = name;
            this.concordionFieldScope = concordionFieldScope;
            this.specificationClass = specificationClass;
        }

        @Override
        public int hashCode() {
            Object[] values = { name, concordionFieldScope, specificationClass };
            return Arrays.hashCode(values);
        }

        @Override
        public boolean equals(Object other) {

            if (other == null) {
                return false;
            }

            if (!(other instanceof ScopedObjectRepositoryKey)) {
                return false;
            }

            ScopedObjectRepositoryKey that = (ScopedObjectRepositoryKey) other;

            if (!this.name.equals(that.name)) {
                return false;
            }

            if (!this.concordionFieldScope.equals(that.concordionFieldScope)) {
                return false;
            }

            if (!this.specificationClass.equals(that.specificationClass)) {
                return false;
            }

            return true;
        }
    }
}

package org.concordion.internal.cache;

/**
 * Created by tim on 30/06/15.
 */
public class CacheKey {
    final String example;
    final Class<?> clas;

    public CacheKey(Class<?> clas, String example) {
        assert clas != null;
        this.clas = clas;
        this.example = example;
    }

    @Override
    public int hashCode() {
        int classHash = clas.hashCode();
        int exampleHash = example == null ? 0 : example.hashCode();

        // use 11 because it's prime
        return classHash * 11 + exampleHash;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof CacheKey)) {
            return super.equals(o);
        }

        CacheKey other = (CacheKey) o;

        boolean classesEqual = clas.equals(other.clas);
        boolean examplesEqual = example == null ? other.example == null : example.equals(other.example);

        return classesEqual && examplesEqual;
    }

    public boolean isForClass(Class<?> aClass) {
        return this.clas.equals(aClass);
    }
}

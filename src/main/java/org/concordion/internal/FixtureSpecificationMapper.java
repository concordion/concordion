package org.concordion.internal;

import org.concordion.api.Resource;
import org.concordion.integration.TestFrameworkProvider;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Maps from a fixture class to a specification and vice-versa.
 * 
 * Encapsulates the rules for fixture class naming:
 * <ul>
 * <li>The fixture class must be in the same package as its specification.
 * <li>The fixture class must be named the same as the specification, with an optional suffix of "Test" or "Fixture".
 * </ul>
 */
public class FixtureSpecificationMapper {

    private static ThreadLocal serviceLoaderHolder  = new ThreadLocal<ServiceLoader<TestFrameworkProvider>>() {
        @Override
        protected ServiceLoader<TestFrameworkProvider> initialValue() {
            return ServiceLoader.load(TestFrameworkProvider.class);
        }
    };

    public static Resource toSpecificationResource(FixtureType fixtureType, String specificationSuffix) {
        String slashedClassName = fixtureType.getFixtureClass().getName().replaceAll("\\.", "/");
        String fixturePath = removeSuffixFromFixtureName(slashedClassName);
        String resourcePath = "/" + fixturePath + "." + specificationSuffix;
        
        return new Resource(resourcePath);
    }
    
    public static String removeSuffixFromFixtureName(String fixtureName) {
        return fixtureName.replaceAll("(Fixture|Test)$", "");
    }

    public static Class<?> findFixtureClass(Resource hrefResource) throws ClassNotFoundException {
        String dottedHrefResource = hrefResource.getPath().replaceFirst("/", "").replace("/", ".");
        String name = dottedHrefResource.substring(0, dottedHrefResource.lastIndexOf("."));
        String[] fixtureNames = {name, name + "Test", name + "Fixture"};
        for (String fixtureName : fixtureNames) {
            Class<?> concordionClass = getFixtureClass(fixtureName);
            if (concordionClass != null) {
                return concordionClass;
            }
        }
        throw new ClassNotFoundException("Unable to find fixture class for '" + hrefResource + "'. " +
                "Fixture class must be named one of " + Arrays.toString(fixtureNames) + ".");
    }

    private static Class<?> getFixtureClass(String name) throws ClassNotFoundException {
        try {
            Class<?> clazz = Class.forName(name);
            ServiceLoader<TestFrameworkProvider> serviceLoader = (ServiceLoader<TestFrameworkProvider>) serviceLoaderHolder.get();
            Iterator<TestFrameworkProvider> iterator = serviceLoader.iterator();
            while (iterator.hasNext()) {
                TestFrameworkProvider provider = iterator.next();
                if (provider.isConcordionFixture(clazz)) {
                    return clazz;
                }
            }
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}

package org.concordion.internal;

import org.concordion.api.Resource;

/**
 * Maps from a fixture class to a specification and vice-versa.
 * <p/>
 * Encapsulates the rules for fixture class naming:
 * <ul>
 * <li>The fixture class must be in the same package as its specification.
 * <li>The fixture class must be named the same as the specification, with an optional suffix of "Test" or "Fixture".
 * </ul>
 */
public class FixtureSpecificationMapper {
    public static Resource toSpecificationResource(Object fixture, String specificationSuffix) {
        String slashedClassName = fixture.getClass().getName().replaceAll("\\.", "/");
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
        Class<?> concordionClass;
        try {
            concordionClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                concordionClass = Class.forName(name + "Test");
            } catch (ClassNotFoundException e1) {
                concordionClass = Class.forName(name + "Fixture"); // FIXED: Issue 47
            }
        }
        return concordionClass;
    }   
}

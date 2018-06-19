package org.concordion.api;

/**
 * An interface to access example element's name and attributes for use mainly in {@link ImplementationStatusModifier}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface ExampleDefinition {

    /**
     * Accessor to get the example's name
     *
     * @return the example's name
     */
    String getName();

    /**
     * Accessor to the example's attribute given the name
     *
     * @param name the name of the attribute
     * @return the attribute value
     */
    String getAttributeValue(String name);

    /**
     * Accessor to the example's attribute given the name and namespace
     *
     * @param localName    the name of the attribute
     * @param namespaceURI the namespace of the attribute
     * @return the attribute value
     */
    String getAttributeValue(String localName, String namespaceURI);

}

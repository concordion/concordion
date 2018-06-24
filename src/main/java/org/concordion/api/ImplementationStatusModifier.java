package org.concordion.api;

/**
 * Examples can be marked as {@code Unimplemented}, {@code ExpectedToFail}, or {@code Ignored} declaratively via
 * c:status attribute.  If the status needs to be determined at runtime, this extension point can be used.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 * @see ImplementationStatus
 */
public interface ImplementationStatusModifier {

    /**
     * Determine an example element's {@code ImplementationStatus}
     *
     * @param exampleDefinition the definition of the example to evaluate
     * @return the status based on the exampleDefinition
     */
    ImplementationStatus getStatusForExample(ExampleDefinition exampleDefinition);

}

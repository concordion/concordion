package org.concordion.api;

/**
 * An extension point to allow for skipping the execution of an example command.  If the method evaluates to true then
 * the the example would be marked as {@link ImplementationStatus#SKIPPED} and all the commands under the example won't
 * be executed.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface ExampleFilter {

    /**
     * Evaluates if an example element should be skipped or not.
     *
     * @param exampleElement the element to evaluate
     * @return <tt>true</tt> if example should be skipped, <tt>false</tt> otherwise
     */
    boolean shouldSkip(Element exampleElement);

}

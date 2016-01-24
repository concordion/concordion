package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.concordion.api.listener.SpecificationProcessingListener;

/**
 * Marks methods that are to be invoked after all examples in the specification are run.
 * 
 * <p>For example:</p>
 * <pre>
 * &#064;AfterSpecification
 * public void logAfterSpecification() {
 *     log("Finished specification");
 * }
 * </pre>
 * 
 * @see BeforeSpecification
 * @see SpecificationProcessingListener
 * 
 * @since 2.0.0
 */
@java.lang.annotation.Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterSpecification {
}

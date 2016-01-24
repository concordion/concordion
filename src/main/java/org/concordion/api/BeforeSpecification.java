package org.concordion.api;

import java.lang.annotation.*;

import org.concordion.api.listener.SpecificationProcessingListener;

/**
 * Marks methods that are to be invoked before any examples in the specification are run.
 * <p>
 * For instance fields that you only want to initialise once per specification (for example browsers or database connections),
 * see {@link ConcordionScoped}.   
 * </p>
 * <p>For example:</p>
 * <pre>
 * &#064;BeforeSpecification
 * public void logBeforeSpecification() {
 *     log("Starting specification");
 * }
 * </pre>
 * 
 * @see AfterSpecification
 * @see SpecificationProcessingListener
 * 
 * @since 2.0.0
 */
@java.lang.annotation.Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeSpecification {
}

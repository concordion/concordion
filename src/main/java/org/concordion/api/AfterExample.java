package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.concordion.api.listener.ExampleListener;
import org.concordion.internal.command.ExampleCommand;

/**
 * Marks a method to be invoked after each example is run. The examples must use the {@link ExampleCommand} for
 * the method to be invoked.
 * <p>
 * The method can optionally take a String parameter annotated with <code>@ExampleName</code>, which will be passed the 
 * example name.
 * </p>
 * <p>For example:</p>
 * <pre>
 * &#064;AfterExample
 * public void logAfterExample(&#064;ExampleName String exampleName) {
 *     log("Finished " + exampleName);
 * }
 * </pre>
 * 
 * @see BeforeExample
 * @see ExampleListener
 * 
 * @since 2.0.0
 */
@java.lang.annotation.Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterExample {
}

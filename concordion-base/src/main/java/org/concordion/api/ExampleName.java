package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a String parameter in methods annotated with {@link BeforeExample} or {@link AfterExample} to indicate that the parameter
 * is to be populated with the name of the example. 
 * 
 * @see BeforeExample
 * @see AfterExample
 * @since 2.0.0
 */
@java.lang.annotation.Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleName {
}

package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a String parameter in the {@link BeforeExample} and {@link AfterExample} methods to indicate that the parameter
 * is to be populated with the name of the example. 
 */
@java.lang.annotation.Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleName {
}

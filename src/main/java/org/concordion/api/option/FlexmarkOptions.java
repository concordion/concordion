package org.concordion.api.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
/**
 * Annotation for the optional field that contains options to be passed to the Flexmark markdown parser.
 */
public @interface FlexmarkOptions {
}

package org.concordion.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcordionScopedField {

    ConcordionFieldScope scope() default ConcordionFieldScope.EXAMPLE;

    String value() default "";

}

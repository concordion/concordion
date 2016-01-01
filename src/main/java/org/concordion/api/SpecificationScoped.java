package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.concordion.internal.ConcordionFieldScope;
import org.concordion.internal.ConcordionScopeDeclaration;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ConcordionScopeDeclaration(scope = ConcordionFieldScope.SPECIFICATION)
public @interface SpecificationScoped {
    String value() default "";
}

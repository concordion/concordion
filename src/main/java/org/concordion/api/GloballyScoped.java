package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.concordion.internal.ConcordionScopeDeclaration;
import org.concordion.internal.ConcordionScopedField;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ConcordionScopeDeclaration(scope = ConcordionScopedField.Scope.GLOBAL)
public @interface GloballyScoped {
    String value() default "";
}

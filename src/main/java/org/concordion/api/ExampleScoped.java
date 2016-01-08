package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.concordion.internal.ScopeType;
import org.concordion.internal.ScopeDeclaration;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ScopeDeclaration(scope = ScopeType.EXAMPLE)
public @interface ExampleScoped {
    String value() default "";
}
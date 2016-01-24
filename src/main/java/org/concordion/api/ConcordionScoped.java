package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For fields that are expensive to setup, this annotation specifies the scope of the field. For example, a field
 * marked with <code>@ConcordionScoped(Scope.SPECIFICATION)</code>, would be constructed once per specification.
 * <p>
 * Without this annotation, instance fields are reinitialised for every example (that uses the example command).
 * </p>
 * <p>
 * This annotation can only be used with fields of type {@link ScopedObjectHolder}. 
 * </p>
 * <p><b>Example:</b></p>
 * <pre>
 * &#064;ConcordionScoped(Scope.SPECIFICATION)
 * private ScopedObjectHolder&lt;Browser&gt; browserHolder = new ScopedObjectHolder&lt;Browser&gt;() {
 *     &#064;Override
 *     protected Browser create() {
 *         return new Browser();
 *     }
 *     
 *     &#064;Override
 *     protected void destroy(Browser browser) {
 *         browser.close();
 *     };
 * };
 * </pre> 
 * 
 * @since 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcordionScoped {
    Scope value();
}

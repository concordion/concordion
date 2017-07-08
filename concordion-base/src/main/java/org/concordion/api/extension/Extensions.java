package org.concordion.api.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Defines a list of classes to be added to Concordion as extensions. 
 *  The specified classes must implement {@link ConcordionExtension} or {@link ConcordionExtensionFactory}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Extensions {
    Class<?>[] value();
}

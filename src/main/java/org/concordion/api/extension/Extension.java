package org.concordion.api.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marks fields to be added to Concordion as extensions.
 *  Fields with this annotation must be public and must implement <code>org.concordion.api.extension.ConcordionExtension</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Extension {
}

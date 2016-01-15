package org.concordion.api.extension;

import java.lang.annotation.*;

import org.concordion.api.Scope;

/**
 *  Marks fields to be added to Concordion as extensions.
 *  Fields with this annotation must be public and must implement <code>org.concordion.api.extension.ConcordionExtension</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Extension {
}

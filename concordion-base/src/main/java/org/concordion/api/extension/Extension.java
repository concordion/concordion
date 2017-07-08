package org.concordion.api.extension;

import java.lang.annotation.*;

/**
 *  Marks fields to be added to Concordion as extensions.
 *  Fields with this annotation must implement <code>org.concordion.api.extension.ConcordionExtension</code>.
 *  As of Concordion 2.0.0, fields with this annotation are no longer required to have public visibility.  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Extension {
}

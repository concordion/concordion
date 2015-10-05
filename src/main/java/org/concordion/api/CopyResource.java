package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CopyResource {

	public enum InsertType {
	   LINKED, EMBEDDED
	}
	
	public String[] sourceFiles() default {};
	public InsertType insertType() default InsertType.EMBEDDED;
	public boolean removeDefaultCSS() default false;
}

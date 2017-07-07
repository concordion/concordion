package org.concordion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Includes resource files such as CSS, JavaScript, images, etc along with the generated specification.
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConcordionResources {

	public enum InsertType {
	   LINKED, EMBEDDED
	}
	
	/**
	 * Specify one or more files to add to the generated specification:
	 * <ul>
	 * 	<li>File names starting with '/' are relative to the root folder</li>
	 * 	<li>File names not starting with '/' are relative to the class the annotation is applied to</li>
	 * 	<li>Supports wild card characters '*' and '?' in the file name ("/images/*.png", "/css/*.*")</li>
	 * 	<li>CSS and JS files are automatically added to the &lt;head&gt; section (see insertType parameter)
	 * 	<li>All other file types must be manually added to the specification</li>
	 * </ul>
	 *  
	 * @return Array of files
	 */
	public String[] value() default {};
	
	/**
	 * Controls how CSS and JavaScript files are added to the specification, choices are LINKED (default) or EMBEDDED
	 * 
	 * @return InsertType 
	 */
	public InsertType insertType() default InsertType.LINKED;
	
	/**
	 * @return If false will remove the default Concordion CSS styling (defaults to true)
	 */
	public boolean includeDefaultStyling() default true;
}

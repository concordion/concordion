package org.concordion.api.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Options that can be applied to specifications to configure aspects of Concordion. 
 *
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConcordionOptions {
    /**
     * Provides the ability to extend the Markdown syntax, when using Markdown format specifications.
     * @return list of extensions to be applied
     */
    MarkdownExtensions[] markdownExtensions() default {};

    /**
     * If set, a copy of the HTML specification source will be saved to the specified directory.
     * <p>
     * The target directory can include system property values in the path, by wrapping the system property key in `${` and `}`. For example:
     * <code>@ConcordionOptions(copySourceHtmlToDir="${java.io.tmpdir}/output")</code>
     * will write to the output directory under the system temp folder.
     * </p>
     * <p>
     * This is useful when investigating the output of converters, such as when using Markdown format, 
     * and you want to see the generated HTML.
     * </p> 
     * @return the directory to log a copy of the HTML source to, optionally including system properties wrapped in `${` and `}`
     */
    String copySourceHtmlToDir() default "";

    /**
     * Additional namespaces to be declared in the input specification.
     * This is used when the specification uses commands from extensions that have different namespaces to the normal Concordion commands.
     * Currently this only applies to the Markdown format, since
     * namespaces are declared directly in the HTML when using HTML specifications.
     *
     * @return list of pairs of Strings to be applied, where each pair contains a namespace prefix followed by the namespace 
     */
    String[] declareNamespaces() default {};
}

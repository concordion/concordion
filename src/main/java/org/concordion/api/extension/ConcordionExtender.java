package org.concordion.api.extension;

import org.concordion.api.Command;
import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.api.Target;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertTrueListener;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.api.listener.VerifyRowsListener;

/**
 * Allows {@link ConcordionExtension}s to add features to Concordion.
 */
public interface ConcordionExtender {
    /**
     * Adds a command to Concordion.  
     * @param namespaceURI the URI to be used for the namespace of the command.  Must not be <code>concordion.org</code>. 
     * @param commandName the name to be used for the command.  The fully qualified name composed of the <code>namespaceURI</code> and
     * <code>commandName</code> must be used to reference the command in the Concordion specification. 
     * @param command the command to be executed
     * @return this
     */
    ConcordionExtender withCommand(String namespaceURI, String commandName, Command command);
    
    /**
     * Adds a listener to <code>concordion:assertEquals</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withAssertEqualsListener(AssertEqualsListener listener);

    /**
     * Adds a listener to <code>concordion:assertTrue</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withAssertTrueListener(AssertTrueListener listener);

    /**
     * Adds a listener to <code>concordion:assertFalse</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withAssertFalseListener(AssertFalseListener listener);

    /**
     * Adds a listener to <code>concordion:execute</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withExecuteListener(ExecuteListener executeListener);

    /**
     * Adds a listener to <code>concordion:run</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withRunListener(RunListener listener);
    
    /**
     * Adds a listener to <code>concordion:verifyRows</code> commands.
     * @param listener 
     * @return this
     */
    ConcordionExtender withVerifyRowsListener(VerifyRowsListener listener);

    /**
     * Adds a listener that is invoked when an uncaught {@link Throwable} is thrown by a command,
     * including commands that have been added using {@link #withCommand(String, String, Command)}.
     * @param throwableListener 
     * @return this
     */
    ConcordionExtender withThrowableListener(ThrowableCaughtListener throwableListener);
    
    /**
     * Adds a listener that is invoked when Concordion parses the specification document, providing 
     * access to the parsed document.
     * @param listener 
     * @return this
     */
    ConcordionExtender withDocumentParsingListener(DocumentParsingListener listener);

    /**
     * Adds a listener that is invoked before and after Concordion has processed the specification,
     * providing access to the specification resource and root element. 
     * @param listener
     * @return this
     */
    ConcordionExtender withSpecificationProcessingListener(SpecificationProcessingListener listener);

    /**
     * Adds a listener that is invoked when a Concordion instance is built, providing access to the {@link Target}
     * to which resources can be written.  
     * @param listener
     * @return this
     */
    ConcordionExtender withBuildListener(ConcordionBuildListener listener);
    
    /**
     * Copies a resource to the Concordion output.
     * @param sourcePath
     * @param targetResource 
     * @return this
     */
    ConcordionExtender withResource(String sourcePath, Resource targetResource);
    
    /**
     * Embeds the given CSS in the Concordion output.
     * @param css
     * @return this
     */
    ConcordionExtender withEmbeddedCSS(String css);
    
    /**
     * Copies the given CSS file to the Concordion output folder, and adds a link to the CSS in the &lt;head&gt; section of the Concordion HTML.  
     * @param cssPath
     * @param targetResource
     * @return this
     */
    ConcordionExtender withLinkedCSS(String cssPath, Resource targetResource);
    
    /**
     * Embeds the given JavaScript in the Concordion output.
     * @param javaScript
     * @return this
     */
    ConcordionExtender withEmbeddedJavaScript(String javaScript);
    
    /**
     * Copies the given JavaScript file to the Concordion output folder, and adds a link to the JavaScript in the &lt;head&gt; section of the Concordion HTML.  
     * @param jsPath
     * @param targetResource
     * @return this
     */
    ConcordionExtender withLinkedJavaScript(String jsPath, Resource targetResource);

    /**
     * Overrides the source that the Concordion specifications are read from.
     * @param source the new source 
     * @return this
     */
    ConcordionExtender withSource(Source source);

    /**
     * Overrides the target that the Concordion specifications are written to.
     * @param source the new target 
     * @return this
     */
    ConcordionExtender withTarget(Target target);
}

package org.concordion.api.extension;

import org.concordion.api.Command;
import org.concordion.api.Resource;
import org.concordion.api.RunStrategy;
import org.concordion.api.Source;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.Target;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertTrueListener;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.SetListener;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.command.RunCommand;

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
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withAssertEqualsListener(AssertEqualsListener listener);

    /**
     * Adds a listener to <code>concordion:assertTrue</code> commands.
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withAssertTrueListener(AssertTrueListener listener);

    /**
     * Adds a listener to <code>concordion:assertFalse</code> commands.
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withAssertFalseListener(AssertFalseListener listener);

    /**
     * Adds a listener to <code>concordion:execute</code> commands.
     * @param executeListener the listener
     * @return this
     */
    ConcordionExtender withExecuteListener(ExecuteListener executeListener);

    /**
     * Adds a listener to <code>concordion:set</code> commands.
     * @param setListener the listener
     * @return this
     */
    ConcordionExtender withSetListener(SetListener setListener);

    /**
     * Adds a listener to <code>concordion:run</code> commands.
<<<<<<< HEAD
     * @param listener the listener
=======
     * @param listener  the listener
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withRunListener(RunListener listener);
    
    /**
     * Adds a listener to <code>concordion:verifyRows</code> commands.
<<<<<<< HEAD
     * @param listener the listener
=======
     * @param listener  the listener
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withVerifyRowsListener(VerifyRowsListener listener);

    /**
     * Adds a listener that is invoked when an uncaught {@link Throwable} is thrown by a command,
     * including commands that have been added using {@link #withCommand(String, String, Command)}.
<<<<<<< HEAD
     * @param throwableListener the listener
=======
     * @param throwableListener  the listener
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withThrowableListener(ThrowableCaughtListener throwableListener);
    
    /**
     * Adds a listener that is invoked when Concordion parses the specification document, providing 
     * access to the parsed document.
<<<<<<< HEAD
     * @param listener the listener
=======
     * @param listener  the listener
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withDocumentParsingListener(DocumentParsingListener listener);

    /**
     * Adds a listener that is invoked before and after Concordion has processed the specification,
     * providing access to the specification resource and root element. 
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withSpecificationProcessingListener(SpecificationProcessingListener listener);

    /**
     * Adds a listener that is invoked when a Concordion instance is built, providing access to the {@link Target}
     * to which resources can be written.  
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withBuildListener(ConcordionBuildListener listener);
    
    /**
     * Copies a resource to the Concordion output.
<<<<<<< HEAD
     * @param sourcePath Storage Path
     * @param targetResource Target Resource
=======
     * @param sourcePath Path to source
     * @param targetResource target resource
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withResource(String sourcePath, Resource targetResource);
    
    /**
     * Embeds the given CSS in the Concordion output.
<<<<<<< HEAD
     * @param css CSS
=======
     * @param css The CSS
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withEmbeddedCSS(String css);
    
    /**
     * Copies the given CSS file to the Concordion output folder, and adds a link to the CSS in the &lt;head&gt; section of the Concordion HTML.  
<<<<<<< HEAD
     * @param cssPath CSS Path
     * @param targetResource Target Resource
=======
     * @param cssPath Path to CSS
     * @param targetResource Target resource
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withLinkedCSS(String cssPath, Resource targetResource);
    
    /**
     * Embeds the given JavaScript in the Concordion output.
<<<<<<< HEAD
     * @param javaScript javaScript
=======
     * @param javaScript JavaScript to embedd
>>>>>>> origin/master
     * @return this
     */
    ConcordionExtender withEmbeddedJavaScript(String javaScript);
    
    /**
     * Copies the given JavaScript file to the Concordion output folder, and adds a link to the JavaScript in the &lt;head&gt; section of the Concordion HTML.  
     * @param jsPath path to javascript
     * @param targetResource target resource
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
     * @param target the new target 
     * @return this
     */
    ConcordionExtender withTarget(Target target);

    /**
     * Overrides the locator for Concordion specifications.
     * @param locator the new specification locator
     * @return this
     */
    ConcordionExtender withSpecificationLocator(SpecificationLocator locator);

    /**
     * Sets the strategy for the {@link RunCommand}.
     * @param runStrategy the new strategy for running the specifications
     * @return this
     */
    ConcordionExtender withRunStrategy(RunStrategy runStrategy);
}

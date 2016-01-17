package org.concordion.api.extension;

import org.concordion.api.*;
import org.concordion.api.listener.*;
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
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withRunListener(RunListener listener);
    
    /**
     * Adds a listener to <code>concordion:verifyRows</code> commands.
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withVerifyRowsListener(VerifyRowsListener listener);

    /**
     * Adds a listener that is invoked when an uncaught {@link Throwable} is thrown by a command,
     * including commands that have been added using {@link #withCommand(String, String, Command)}.
     * @param throwableListener the listener
     * @return this
     */
    ConcordionExtender withThrowableListener(ThrowableCaughtListener throwableListener);
    
    /**
     * Adds a listener that is invoked when Concordion parses the specification document, providing 
     * access to the parsed document.
     * @param listener the listener
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
     * Adds a listener that is invoked before and after Concordion has processed the example,
     * providing access to the example node. 
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withExampleListener(ExampleListener listener);
    
    /**
     * Adds a listener that is invoked when a Concordion instance is built, providing access to the {@link Target}
     * to which resources can be written.  
     * @param listener the listener
     * @return this
     */
    ConcordionExtender withBuildListener(ConcordionBuildListener listener);
    
    /**
     * Copies a resource to the Concordion output.
     * @param sourcePath Storage Path
     * @param targetResource Target Resource
     * @return this
     */
    ConcordionExtender withResource(String sourcePath, Resource targetResource);
    
    /**
     * Embeds the given CSS in the Concordion output.
     * @param css CSS
     * @return this
     */
    ConcordionExtender withEmbeddedCSS(String css);
    
    /**
     * Embeds the given CSS in the Concordion output.
     * @param css
     * @param append if true appends as last element in head section, else inserts as first element
     * @return
     */
    ConcordionExtender withEmbeddedCSS(String css, boolean append);
    
    /**
     * Copies the given CSS file to the Concordion output folder, and adds a link to the CSS in the &lt;head&gt; section of the Concordion HTML.  
     * @param cssPath CSS Path
     * @param targetResource Target Resource
     * @return this
     */
    ConcordionExtender withLinkedCSS(String cssPath, Resource targetResource);
    
    /**
     * Embeds the given JavaScript in the Concordion output.
     * @param javaScript javaScript
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

    /**
     * Adds a new specification type to the types that can be handled (by default HTML and Markdown are supported). 
     * @param typeSuffix the suffix of the file type to map this to
     * @param specificationConverter converts the specification to HTML format
     * @return this
     */
    ConcordionExtender withSpecificationType(String typeSuffix, SpecificationConverter specificationConverter);
}

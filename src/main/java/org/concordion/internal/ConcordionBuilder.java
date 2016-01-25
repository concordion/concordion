package org.concordion.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.concordion.Concordion;
import org.concordion.api.Command;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.FailFast;
import org.concordion.api.FullOGNL;
import org.concordion.api.Resource;
import org.concordion.api.RunStrategy;
import org.concordion.api.Source;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.SpecificationReader;
import org.concordion.api.Target;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertTrueListener;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.SetListener;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.command.AssertEqualsCommand;
import org.concordion.internal.command.AssertFalseCommand;
import org.concordion.internal.command.AssertTrueCommand;
import org.concordion.internal.command.EchoCommand;
import org.concordion.internal.command.ExecuteCommand;
import org.concordion.internal.command.LocalTextDecorator;
import org.concordion.internal.command.RunCommand;
import org.concordion.internal.command.SetCommand;
import org.concordion.internal.command.SpecificationCommand;
import org.concordion.internal.command.ThrowableCatchingDecorator;
import org.concordion.internal.command.ThrowableCaughtPublisher;
import org.concordion.internal.command.VerifyRowsCommand;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.listener.BreadcrumbRenderer;
import org.concordion.internal.listener.DocumentStructureImprover;
import org.concordion.internal.listener.JavaScriptEmbedder;
import org.concordion.internal.listener.JavaScriptLinker;
import org.concordion.internal.listener.MetadataCreator;
import org.concordion.internal.listener.PageFooterRenderer;
import org.concordion.internal.listener.RunResultRenderer;
import org.concordion.internal.listener.SpecificationExporter;
import org.concordion.internal.listener.StylesheetEmbedder;
import org.concordion.internal.listener.StylesheetLinker;
import org.concordion.internal.listener.ThrowableRenderer;
import org.concordion.internal.listener.VerifyRowsResultRenderer;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;

public class ConcordionBuilder implements ConcordionExtender {

    private List<ConcordionBuildListener> listeners = new ArrayList<ConcordionBuildListener>();

    public static final String NAMESPACE_CONCORDION_2007 = "http://www.concordion.org/2007/concordion";
    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private static final String PROPERTY_EXTENSIONS = "concordion.extensions";
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/concordion/internal/resource/embedded.css";
    
    private static File baseOutputDir;
    private SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();
    private Source source = null;
    private Target target = null;
    private CommandRegistry commandRegistry = new CommandRegistry();
    private DocumentParser documentParser = new DocumentParser(commandRegistry);
    private SpecificationReader specificationReader;
    private EvaluatorFactory evaluatorFactory = new SimpleEvaluatorFactory();
    private SpecificationCommand specificationCommand = new SpecificationCommand();
    private AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();
    private AssertTrueCommand assertTrueCommand = new AssertTrueCommand();
    private AssertFalseCommand assertFalseCommand = new AssertFalseCommand();
    private ExecuteCommand executeCommand = new ExecuteCommand();
    private SetCommand setCommand = new SetCommand();
    private RunCommand runCommand = new RunCommand();
    private VerifyRowsCommand verifyRowsCommand = new VerifyRowsCommand();
    private EchoCommand echoCommand = new EchoCommand();
    private ThrowableCaughtPublisher throwableListenerPublisher = new ThrowableCaughtPublisher();
    private LinkedHashMap<String, Resource> resourceToCopyMap = new LinkedHashMap<String, Resource>();
    private List<SpecificationProcessingListener> specificationProcessingListeners = new ArrayList<SpecificationProcessingListener>();
    private List<Class<? extends Throwable>> failFastExceptions = Collections.<Class<? extends Throwable>>emptyList();
    private boolean builtAlready;
	private IOUtil ioUtil;
	
	{        
        commandRegistry.register("", "specification", specificationCommand);
        
        AssertResultRenderer assertRenderer = new AssertResultRenderer();
        withAssertEqualsListener(assertRenderer);
        withAssertTrueListener(assertRenderer);
        withAssertFalseListener(assertRenderer);
        withVerifyRowsListener(new VerifyRowsResultRenderer());
        withDocumentParsingListener(new DocumentStructureImprover());
        withDocumentParsingListener(new MetadataCreator());
	}

    public ConcordionBuilder withSource(Source source) {
        this.source = source;
        return this;
    }

    public ConcordionBuilder withTarget(Target target) {
        this.target = target;
        return this;
    }

    public ConcordionBuilder withSpecificationLocator(SpecificationLocator specificationLocator) {
        this.specificationLocator = specificationLocator;
        return this;
    }

    public ConcordionBuilder withEvaluatorFactory(EvaluatorFactory evaluatorFactory) {
        this.evaluatorFactory = evaluatorFactory;
        return this;
    }
    
    public ConcordionBuilder withThrowableListener(ThrowableCaughtListener throwableListener) {
        throwableListenerPublisher.addThrowableListener(throwableListener);
        return this;
    }

    public ConcordionBuilder withAssertEqualsListener(AssertEqualsListener listener) {
        assertEqualsCommand.addAssertEqualsListener(listener);
        return this;
    }
    
    public ConcordionBuilder withAssertTrueListener(AssertTrueListener listener) {
        assertTrueCommand.addAssertListener(listener);
        return this;
    }
    
    public ConcordionBuilder withAssertFalseListener(AssertFalseListener listener) {
        assertFalseCommand.addAssertListener(listener);
        return this;
    }
    
    public ConcordionBuilder withVerifyRowsListener(VerifyRowsListener listener) {
        verifyRowsCommand.addVerifyRowsListener(listener);
        return this;
    }
    
    public ConcordionBuilder withRunListener(RunListener listener) {
        runCommand.addRunListener(listener);
        return this;
    }
    
    @Override
    public ConcordionExtender withRunStrategy(RunStrategy runStrategy) {
        runCommand.setRunStrategy(runStrategy);
        return this;
    }

    public ConcordionBuilder withExecuteListener(ExecuteListener listener) {
        executeCommand.addExecuteListener(listener);
        return this;
    }

    public ConcordionExtender withSetListener(SetListener setListener) {
        setCommand.addSetListener(setListener);
        return this;
    }

    public ConcordionBuilder withDocumentParsingListener(DocumentParsingListener listener) {
        documentParser.addDocumentParsingListener(listener);
        return this;
    }

    public ConcordionBuilder withSpecificationProcessingListener(SpecificationProcessingListener listener) {
        specificationProcessingListeners.add(listener);
        return this;
    }

    public ConcordionBuilder withBuildListener(ConcordionBuildListener listener) {
        listeners.add(listener);
        return this;
    }
    
    private ConcordionBuilder withApprovedCommand(String namespaceURI, String commandName, Command command) {
        ThrowableCatchingDecorator throwableCatchingDecorator = new ThrowableCatchingDecorator(new LocalTextDecorator(command), failFastExceptions);
        throwableCatchingDecorator.addThrowableListener(throwableListenerPublisher);
        Command decoratedCommand = throwableCatchingDecorator;
        commandRegistry.register(namespaceURI, commandName, decoratedCommand);
        return this;
    }

    public ConcordionBuilder withCommand(String namespaceURI, String commandName, Command command) {
        Check.notEmpty(namespaceURI, "Namespace URI is mandatory");
        Check.notEmpty(commandName, "Command name is mandatory");
        Check.notNull(command, "Command is null");
        Check.isFalse(namespaceURI.contains("concordion.org"),
                "The namespace URI for user-contributed command '" + commandName + "' "
              + "must not contain 'concordion.org'. Use your own domain name instead.");
        return withApprovedCommand(namespaceURI, commandName, command);
    }
    
    public ConcordionBuilder withResource(String sourcePath, Resource targetResource) {
        resourceToCopyMap.put(sourcePath, targetResource);
        return this;
    }

    public ConcordionBuilder withEmbeddedCSS(String css) {
        StylesheetEmbedder embedder = new StylesheetEmbedder(css);
        withDocumentParsingListener(embedder);
        return this;
    }
    
    public ConcordionBuilder withLinkedCSS(String cssPath, Resource targetResource) {
        withResource(cssPath, targetResource);
        StylesheetLinker cssLinker = new StylesheetLinker(targetResource);
        withDocumentParsingListener(cssLinker);
        withSpecificationProcessingListener(cssLinker);
        return this;
    }

    public ConcordionBuilder withEmbeddedJavaScript(String javaScript) {
        JavaScriptEmbedder embedder = new JavaScriptEmbedder(javaScript);
        withDocumentParsingListener(embedder);
        return this;
    }

    public ConcordionBuilder withLinkedJavaScript(String jsPath, Resource targetResource) {
        withResource(jsPath, targetResource);
        JavaScriptLinker javaScriptLinker = new JavaScriptLinker(targetResource);
        withDocumentParsingListener(javaScriptLinker);
        withSpecificationProcessingListener(javaScriptLinker);
        return this;
    }
    
    public Concordion build() {
        Check.isFalse(builtAlready, "ConcordionBuilder currently does not support calling build() twice");
        builtAlready = true;
        
        if (ioUtil == null) {
        	ioUtil = new IOUtil();
        }
        
        withThrowableListener(new ThrowableRenderer(ioUtil));
        withRunListener(new RunResultRenderer(ioUtil));
        String stylesheetContent = ioUtil.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
        withEmbeddedCSS(stylesheetContent);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "run", runCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "set", setCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-equals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertEquals", assertEqualsCommand);

        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-true", assertTrueCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertTrue", assertTrueCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-false", assertFalseCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertFalse", assertFalseCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "verify-rows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "verifyRows", verifyRowsCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "echo", echoCommand);
        
        if (source == null) {
        	source = new ClassPathSource(ioUtil);
        }

        if (target == null) {
            target = new FileTarget(getBaseOutputDir(), ioUtil);
        }
        XMLParser xmlParser = new XMLParser();
        
        specificationCommand.addSpecificationListener(new BreadcrumbRenderer(source, xmlParser));
        specificationCommand.addSpecificationListener(new PageFooterRenderer(target));

        specificationReader = new XMLSpecificationReader(source, xmlParser, documentParser);        

        addExtensions();
        copyResources();

        addSpecificationListeners();

        SpecificationExporter exporter = new SpecificationExporter(target);
        specificationCommand.addSpecificationListener(exporter);
        specificationCommand.setSpecificationDescriber(exporter);
        
        announceBuildCompleted();
        
        return new Concordion(specificationLocator, specificationReader, evaluatorFactory);
    }

    private void addSpecificationListeners() {
        for (SpecificationProcessingListener listener : specificationProcessingListeners) {
            specificationCommand.addSpecificationListener(listener);
        }
    }

    private void copyResources() {
        for (Entry<String, Resource> resourceToCopy : resourceToCopyMap.entrySet()) {
            String sourcePath = resourceToCopy.getKey();
            Resource targetResource = resourceToCopy.getValue();
            try {
                InputStream inputStream = source.createInputStream(new Resource(sourcePath));
                target.copyTo(targetResource, inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy " + sourcePath + " to target " + targetResource, e);
            }
        }
    }
    
    private void announceBuildCompleted() {
		for (ConcordionBuildListener buildListener : listeners) {
			buildListener.concordionBuilt(new ConcordionBuildEvent(target));
		}
	}

    private void addExtensions() {
        String extensionProp = System.getProperty(PROPERTY_EXTENSIONS);
        if (extensionProp != null) {
            String[] extensions = extensionProp.split("\\s*,\\s*");
            for (String className : extensions) {
                addExtension(className);
            }
        }
    }

    private void addExtension(String className) {
        Class<?> extensionClass;
        try {
            extensionClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find extension '" + className + "' on classpath", e);
        }
        Object extensionObject;
        try {
            extensionObject = extensionClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate extension '" + className + "'", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Extension '" + className + "' or constructor are inaccessible", e);
        }

        ConcordionExtension extension;
        try {
            extension = (ConcordionExtension) extensionObject;
        } catch (ClassCastException e) {
            try {
                ConcordionExtensionFactory factory = (ConcordionExtensionFactory) extensionObject;
                extension = factory.createExtension();
            } catch (ClassCastException e1) {
                String message = String.format("Extension class '%s' must implement '%s' or '%s'", className,
                        ConcordionExtension.class.getName(), ConcordionExtensionFactory.class.getName());
                throw new RuntimeException(message);
            }
        }
        extension.addTo(this);
    }

    public static File getBaseOutputDir() {
        if (baseOutputDir == null) {
            String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
            if (outputPath != null) {
                baseOutputDir = new File(outputPath);
            } else {
                baseOutputDir = new File(System.getProperty("java.io.tmpdir"), "concordion");
            } 
        }
        return baseOutputDir;
    }

    public ConcordionBuilder withFailFast(Class<? extends Throwable>[] failFastExceptions) {
        this.failFastExceptions = Arrays.asList(failFastExceptions);
        return this;
    }

    public ConcordionBuilder withFixture(Object fixture) {
        if (fixture.getClass().isAnnotationPresent(FailFast.class)) {
            FailFast failFastAnnotation = fixture.getClass().getAnnotation(FailFast.class);
            Class<? extends Throwable>[] failFastExceptions = failFastAnnotation.onExceptionType();
            withFailFast(failFastExceptions);
        }
        if (fixture.getClass().isAnnotationPresent(FullOGNL.class)) {
            withEvaluatorFactory(new OgnlEvaluatorFactory());
        }
        return this;
    }
    
    public ConcordionBuilder withIOUtil(IOUtil ioUtil) {
    	this.ioUtil = ioUtil;
    	return this;
    }
}

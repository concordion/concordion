package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.api.listener.*;
import org.concordion.internal.command.*;
import org.concordion.internal.listener.*;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

public class ConcordionBuilder implements ConcordionExtender {

    private Announcer<ConcordionBuildListener> listeners = Announcer.to(ConcordionBuildListener.class);

    public static final String NAMESPACE_CONCORDION_2007 = "http://www.concordion.org/2007/concordion";
    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private static final String PROPERTY_EXTENSIONS = "concordion.extensions";
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/concordion/internal/resource/embedded.css";
    
    private static File baseOutputDir;
    private SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();
    private Source source = new ClassPathSource();
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
    private MatchRowsCommand matchRowsCommand = new MatchRowsCommand();
    private EchoCommand echoCommand = new EchoCommand();
    private ExampleCommand exampleCommand = new ExampleCommand();
    private ThrowableCaughtPublisher throwableListenerPublisher = new ThrowableCaughtPublisher();
    private LinkedHashMap<String, Resource> resourceToCopyMap = new LinkedHashMap<String, Resource>();
    private List<SpecificationProcessingListener> specificationProcessingListeners = new ArrayList<SpecificationProcessingListener>();
    private List<Class<? extends Throwable>> failFastExceptions = Collections.<Class<? extends Throwable>>emptyList();
    private boolean builtAlready;
    private Object fixture;

    {
        withThrowableListener(new ThrowableRenderer());
        
        commandRegistry.register("", "specification", specificationCommand);
        
        AssertResultRenderer assertRenderer = new AssertResultRenderer();
        withAssertEqualsListener(assertRenderer);
        withAssertTrueListener(assertRenderer);
        withAssertFalseListener(assertRenderer);
        withVerifyRowsListener(new RowsResultRenderer());
        withMatchRowsListener(new RowsResultRenderer());
        withRunListener(new RunResultRenderer());
        withDocumentParsingListener(new DocumentStructureImprover());
        withDocumentParsingListener(new MetadataCreator());
        String stylesheetContent = IOUtil.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
        withEmbeddedCSS(stylesheetContent);
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
    
    public ConcordionBuilder withVerifyRowsListener(RowsListener listener) {
        verifyRowsCommand.addVerifyRowsListener(listener);
        return this;
    }

    public ConcordionBuilder withMatchRowsListener(RowsListener listener) {
        matchRowsCommand.addVerifyRowsListener(listener);
        return this;
    }
    
    public ConcordionBuilder withRunListener(RunListener listener) {
        runCommand.addRunListener(listener);
        return this;
    }
    
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
        listeners.addListener(listener);
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
    
    public Concordion build() throws UnableToBuildConcordionException {
        Check.isFalse(builtAlready, "ConcordionBuilder currently does not support calling build() twice");
        builtAlready = true;
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "run", runCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "set", setCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "example", exampleCommand);

        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-equals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertEquals", assertEqualsCommand);

        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-true", assertTrueCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertTrue", assertTrueCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assert-false", assertFalseCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertFalse", assertFalseCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "verify-rows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "verifyRows", verifyRowsCommand);
        
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "echo", echoCommand);

        if (target == null) {
            target = new FileTarget(getBaseOutputDir());
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

        exampleCommand.setSpecificationDescriber(exporter);
        
        listeners.announce().concordionBuilt(new ConcordionBuildEvent(target));

        try {
            return new Concordion(specificationLocator, specificationReader, evaluatorFactory, fixture);
        } catch (IOException e) {
            throw new UnableToBuildConcordionException(e);
        }
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
        this.fixture = fixture;
        return withFixtureForAnnotationsOnly(fixture);
    }

    public ConcordionBuilder withFixtureForAnnotationsOnly(Object fixture) {
        if (fixture == null) {
            return this;
        }

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
}

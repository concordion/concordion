package org.concordion.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vladsch.flexmark.util.data.DataSet;
import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.api.ConcordionResources.InsertType;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.api.listener.*;
import org.concordion.api.option.ConcordionOptions;
import org.concordion.api.option.MarkdownExtensions;
import org.concordion.internal.command.*;
import org.concordion.internal.command.executeCommand.ExecuteCommand;
import org.concordion.internal.extension.ExtensionChecker;
import org.concordion.internal.listener.*;
import org.concordion.internal.parser.flexmark.MarkdownConverter;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.SimpleFormatter;

public class ConcordionBuilder implements ConcordionExtender {

    private List<ConcordionBuildListener> listeners = new ArrayList<ConcordionBuildListener>();

    public static final String NAMESPACE_CONCORDION_2007 = "http://www.concordion.org/2007/concordion";
    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private static final String PROPERTY_EXTENSIONS = "concordion.extensions";
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/concordion/internal/resource/embedded.css";

    private static File baseOutputDir;
    private SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();
    private Map<SourceType, Source> sources = new HashMap<SourceType, Source>();
    private Target target = null;
    private CommandRegistry commandRegistry = new CommandRegistry();
    private DocumentParser documentParser = new DocumentParser(commandRegistry);
    private SpecificationReader specificationReader;
    private EvaluatorFactory evaluatorFactory = new SimpleEvaluatorFactory();
    private SpecificationCommand specificationCommand = new SpecificationCommand();
    private AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();
    private AssertTrueCommand assertTrueCommand = new AssertTrueCommand();
    private AssertFalseCommand assertFalseCommand = new AssertFalseCommand();
    private ExampleCommand exampleCommand = new ExampleCommand();
    private ExecuteCommand executeCommand = new ExecuteCommand();
    private SetCommand setCommand = new SetCommand();
    private RunCommand runCommand = new RunCommand();
    private VerifyRowsCommand verifyRowsCommand = new VerifyRowsCommand();
    private EchoCommand echoCommand = new EchoCommand();
    private ThrowableCaughtPublisher throwableListenerPublisher = new ThrowableCaughtPublisher();
    private LinkedHashMap<String, Resource> resourceToCopyMap = new LinkedHashMap<String, Resource>();
    private List<SpecificationProcessingListener> specificationProcessingListeners = new ArrayList<SpecificationProcessingListener>();
    private List<ThrowableCaughtListener> throwableCaughtListeners = new ArrayList<ThrowableCaughtListener>();
    private List<Class<? extends Throwable>> failFastExceptions = Collections.<Class<? extends Throwable>>emptyList();
    private boolean builtAlready;
    private FixtureType fixtureType;
    private MarkdownConverter markdownConverter = new MarkdownConverter();
    private XhtmlConverter xhtmlConverter = new XhtmlConverter();

    private List<SpecificationType> specificationTypes = new ArrayList<SpecificationType>();
    private Set<SpecificationConverter> specificationConverters = new HashSet<SpecificationConverter>();

    private FileTarget copySourceHtmlTarget;
    private SpecificationProcessingListener pageFooterRenderer;
    private BreadcrumbRenderer breadcrumbRenderer;
    private RunnerFactory runnerFactory;
    private DataSet flexmarkOptions;

    {
        ExtensionChecker.checkForOutdatedExtensions();
        commandRegistry.register("", "specification", specificationCommand);

        withSource(new ClassPathSource());

        AssertResultRenderer assertRenderer = new AssertResultRenderer();
        withAssertEqualsListener(assertRenderer);
        withAssertTrueListener(assertRenderer);
        withAssertFalseListener(assertRenderer);
        withVerifyRowsListener(new VerifyRowsResultRenderer());
        withDocumentParsingListener(new DocumentStructureImprover());
        withDocumentParsingListener(new MetadataCreator());
        withSpecificationType("html", null);
        withSpecificationType("xhtml", xhtmlConverter);
        withSpecificationType("md", markdownConverter);
        withSpecificationType("markdown", markdownConverter);
        withPageFooterRenderer(new PageFooterRenderer());
        withRunnerFactory(new SystemPropertiesRunnerFactory());
    }

    public ConcordionBuilder withSource(Source source) {
        for (SourceType sourceType : SourceType.values()) {
            withSource(source, sourceType);
        }
        return this;
    }

    public ConcordionBuilder withSource(Source source, SourceType sourceType) {
        sources.put(sourceType, source);
        return this;
    }

    public ConcordionBuilder withTarget(Target target) {
        this.target = target;
        return this;
    }

    public ConcordionBuilder withPageFooterRenderer(SpecificationProcessingListener pageFooterRenderer) {
        this.pageFooterRenderer = pageFooterRenderer;
        return this;
    }

    public ConcordionBuilder withBreadcrumbRenderer(BreadcrumbRenderer breadcrumbRenderer) {
    	this.breadcrumbRenderer = breadcrumbRenderer;
    	return this;
    }

    public ConcordionBuilder withRunnerFactory(RunnerFactory runnerFactory) {
        this.runnerFactory = runnerFactory;
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
        throwableCaughtListeners.add(throwableListener);
        return this;
    }

    private ConcordionBuilder withThrowableListener(int index, ThrowableCaughtListener throwableListener) {
        throwableCaughtListeners.add(index, throwableListener);
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

    public ConcordionBuilder withEmbeddedCSS(String css, boolean append) {
        StylesheetEmbedder embedder = new StylesheetEmbedder(css, append);
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
        return build(true);
    }

    public Concordion build(boolean fullBuild) throws UnableToBuildConcordionException {
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

        Source resourceSource = sources.get(SourceType.RESOURCE);
        Source specificationSource = sources.get(SourceType.SPECIFICATION);

        if (fullBuild) {
            withThrowableListener(0, new ThrowableRenderer(resourceSource));
            withRunListener(new RunResultRenderer(resourceSource));
        }

        FixtureExampleHook fixtureExampleHook = new FixtureExampleHook();
        withOuterExampleListener(fixtureExampleHook);
        withExampleListener(fixtureExampleHook);

        if (target == null) {
            target = new FileTarget(getBaseOutputDir());
        }
        XMLParser xmlParser = new XMLParser();

        if (breadcrumbRenderer == null) {
        	breadcrumbRenderer = new BreadcrumbRenderer(specificationSource, xmlParser, specificationTypes);
        }
        specificationCommand.addSpecificationListener(breadcrumbRenderer);
        specificationCommand.addSpecificationListener(pageFooterRenderer);

        specificationReader = new XMLSpecificationReader(specificationSource, xmlParser, documentParser);
        if (fullBuild) {
            specificationReader.setCopySourceHtmlTarget(copySourceHtmlTarget);
        }

        addExtensions();
        copyResources(resourceSource);

        if (fullBuild) {
            addSpecificationListeners();
            addThrowableListeners();
        }

        SpecificationExporter exporter = new SpecificationExporter(target);
        if (fullBuild) {
            specificationCommand.addSpecificationListener(exporter);
        }
        specificationCommand.setSpecificationDescriber(exporter);

        exampleCommand.setSpecificationDescriber(exporter);
        runCommand.setRunnerFactory(runnerFactory);

        if (fullBuild) {
            announceBuildCompleted();
        }

        try {
            return new Concordion(specificationTypes, specificationLocator, specificationReader, evaluatorFactory, fixtureType);
        } catch (IOException e) {
            throw new UnableToBuildConcordionException(e);
        }
    }

    private void announceBuildCompleted() {
		for (ConcordionBuildListener buildListener : listeners) {
			buildListener.concordionBuilt(new ConcordionBuildEvent(target));
		}
	}

	private void addSpecificationListeners() {
        for (SpecificationProcessingListener listener : specificationProcessingListeners) {
            specificationCommand.addSpecificationListener(listener);
        }
    }

    private void addThrowableListeners() {
        for (ThrowableCaughtListener listener : throwableCaughtListeners) {
            throwableListenerPublisher.addThrowableListener(listener);
        }
    }

    private void copyResources(Source resourceSource) {
        for (Entry<String, Resource> resourceToCopy : resourceToCopyMap.entrySet()) {
            String sourcePath = resourceToCopy.getKey();
            Resource targetResource = resourceToCopy.getValue();
            try {
                InputStream inputStream = resourceSource.createInputStream(new Resource(sourcePath));
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
                String message = SimpleFormatter.format("Extension class '%s' must implement '%s' or '%s'", className,
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

    public ConcordionBuilder withFixtureType(FixtureType fixtureType) {
        this.fixtureType = fixtureType;

        withResources(fixtureType);

        if (fixtureType.declaresFailFast()) {
            withFailFast(fixtureType.getDeclaredFailFastExceptions());
        }
        if (fixtureType.declaresFullOGNL()) {
            withEvaluatorFactory(new OgnlEvaluatorFactory());
        }

        return this;
    }

    public ConcordionBuilder withFixture(Fixture fixture) {
        withFixtureType(fixture.getFixtureType());

        flexmarkOptions = new FlexmarkOptionsLoader().getFlexmarkOptionsForFixture(fixture);
        if (flexmarkOptions != null) {
            configureWith(flexmarkOptions);
        }

        return this;
    }

    public ConcordionExtender withExampleListener(ExampleListener listener) {
		exampleCommand.addExampleListener(listener);
		return this;
	}

    @Override
    public ConcordionExtender withImplementationStatusModifier(ImplementationStatusModifier statusModifier) {
        exampleCommand.setImplementationStatusModifier(statusModifier);
        return this;
    }

    public ConcordionExtender withOuterExampleListener(OuterExampleListener listener) {
        specificationCommand.addOuterExampleListener(listener);
        return this;
    }

    private void withResources(FixtureType fixtureType) {
        boolean includeDefaultStyling = true;

        Source resourceSource = sources.get(SourceType.RESOURCE);
        if (fixtureType.declaresResources()) {
        	ResourceFinder resources = new ResourceFinder(fixtureType);
        	List<ResourceToCopy> sourceFiles = resources.getResourcesToCopy();

        	for (ResourceToCopy sourceFile : sourceFiles) {
    			if (sourceFile.isStyleSheet()) {
    				if (sourceFile.insertType == InsertType.EMBEDDED) {
    					withEmbeddedCSS(resourceSource.readResourceAsString(sourceFile.getResourceName()));
    				} else {
    					withLinkedCSS(sourceFile.getResourceName(), new Resource(sourceFile.getResourceName()));
    				}
    			} else if (sourceFile.isScript()) {
    				if (sourceFile.insertType == InsertType.EMBEDDED) {
    					withEmbeddedJavaScript(resourceSource.readResourceAsString(sourceFile.getResourceName()));
    				} else {
    					withLinkedJavaScript(sourceFile.getResourceName(), new Resource(sourceFile.getResourceName()));
    				}
    			} else {
    				withResource(sourceFile.getResourceName(), new Resource(sourceFile.getResourceName()));
    			}
    		}

        	includeDefaultStyling = resources.includeDefaultStyling();

        	withDocumentParsingListener(new ResourceReferenceRemover(sourceFiles));
        }

        if (includeDefaultStyling) {
        	addDefaultStyling(resourceSource);
        }
    }

    private void addDefaultStyling(Source resourceSource) {
    	String stylesheetContent = resourceSource.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
    	withEmbeddedCSS(stylesheetContent);
    }

	@Override
    public ConcordionBuilder withSpecificationType(String typeSuffix, SpecificationConverter converter) {
	    specificationConverters.add(converter);
	    specificationTypes.add(new SpecificationType(typeSuffix, converter));
	    return this;
	}

    void configureWith(ConcordionOptions options) {
        if (options.markdownExtensions().length > 0) {
            int markdownExtensions = 0;
            for (MarkdownExtensions markdownExtension : options.markdownExtensions()) {
                markdownExtensions = markdownExtensions | markdownExtension.getPegdownExtension();
            }
            markdownConverter.withPegdownExtensions(markdownExtensions);
        }

        if (options.declareNamespaces().length > 0) {
            Map<String, String> namespaces = ConcordionOptionsParser.convertNamespacePairsToMap(options.declareNamespaces());
            markdownConverter.withNamespaceDeclarations(namespaces);
        }

        String location = options.copySourceHtmlToDir();
        if (!location.isEmpty()) {
            location = expandSystemProperties(location);
            copySourceHtmlTarget = new FileTarget(new File(location));
        }
    }

    void configureWith(DataSet flexmarkOptions) {
        if(flexmarkOptions != null) {
            markdownConverter.withFlexmarkOptions(flexmarkOptions);
        }
    }

    private String expandSystemProperties(String location) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(location);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String property = matcher.group(1);
            String value = System.getProperty(property);
            if (value == null) {
                throw new RuntimeException(SimpleFormatter.format("Unable to find system property '%s' in @ConcordionOptions setting copySourceHtmlToDir of '%s'", property, location));
            }
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

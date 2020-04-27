package test.concordion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.vladsch.flexmark.util.data.DataSet;
import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.*;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.concordion.internal.parser.flexmark.FlexmarkMarkdownTranslator;

public class TestRig {

    private Fixture fixture;
    private EvaluatorFactory evaluatorFactory = new SimpleEvaluatorFactory();
    private StubSource stubSource = new StubSource();
    private Source source = stubSource;
    private StubTarget stubTarget = new StubTarget();
    private FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
    private ConcordionExtension extension;
    private String namespaceDeclaration = "xmlns:concordion='" + ConcordionBuilder.NAMESPACE_CONCORDION_2007 + "'";
    private DataSet flexmarkOptionsForFixture;

    public TestRig withFixture(Object fixture) {
        this.fixture = new FixtureInstance(fixture);
        return this;
    }

    public TestRig withNamespaceDeclaration(String prefix, String namespace) {
        namespaceDeclaration += " " + String.format("xmlns:%s='%s'", prefix, namespace);
        return this;
    }

    public TestRig loadFlexmarkOptionsFrom(FixtureInstance fixture) {
        flexmarkOptionsForFixture = new FlexmarkOptionsLoader().getFlexmarkOptionsForFixture(fixture);
        return this;
    }

    public ProcessingResult processFragment(String fragment) {
        return process(wrapFragment(fragment));
    }

    public ProcessingResult processFragment(String fragment, String fixtureName) {
        return process(wrapFragment(fragment), new Resource(fixtureName));
    }

    public ProcessingResult processFragment(String resourceLocation, String head, String fragment) {
        return process(resourceLocation, wrapFragment(head, fragment));
    }

    public ProcessingResult processMarkdownFragment(String markdown) {
        FlexmarkMarkdownTranslator markdownParser = new FlexmarkMarkdownTranslator(0, flexmarkOptionsForFixture, Collections.emptyMap(), "concordion");
        String html = markdownParser.markdownToHtml(markdown);
        return processFragment(html);
    }

    public ProcessingResult process(Resource resource) {
        EventRecorder eventRecorder = new EventRecorder();

        if (fixture == null) {
            fixture = new FixtureInstance(new DummyFixture());
            withResource(new Resource("/test/concordion/Dummy.html"), "<html/>");
        } else {
            withResource(new ClassNameBasedSpecificationLocator().locateSpecification(fixture.getFixtureType(), "html"), "<html/>");
        }
        ConcordionBuilder concordionBuilder = new ConcordionBuilder()
            .withAssertEqualsListener(eventRecorder)
            .withThrowableListener(eventRecorder)
            .withSource(source)
            .withEvaluatorFactory(evaluatorFactory)
            .withTarget(stubTarget)
            .withFixture(fixture);

        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);

        if (extension != null) {
            extension.addTo(concordionBuilder);
        }
        Concordion concordion = null;
        try {
            concordion = concordionBuilder.build();
        } catch (UnableToBuildConcordionException e) {
            throw new RuntimeException("Test rig failed to build concordion", e);
        }

        try {

            SummarizingResultRecorder resultSummary = new SummarizingResultRecorder();
            concordion.override(resource);
            List<String> examples = concordion.getExampleNames(fixture.getFixtureType());
            if (!examples.isEmpty()) {
                for (String example : examples) {
                    resultSummary.record(concordion.processExample(fixture, example));
                }
            }
            concordion.finish();

            String xml = stubTarget.getWrittenString(resource);
            return new ProcessingResult(resultSummary, eventRecorder, xml);
        } catch (IOException e) {
            throw new RuntimeException("Test rig failed to process specification", e);
        }
    }

    public ProcessingResult process(String html) {
        return process("/testrig", html);
    }

    public ProcessingResult process(String resourceLocation, String html) {
        Resource resource = new Resource(resourceLocation);
        return process(html, resource);
    }

    private ProcessingResult process(String html, Resource resource) {
        withResource(resource, html);
        return process(resource);
    }

    private String wrapFragment(String fragment) {
        fragment = "<body><fragment>" + fragment + "</fragment></body>";
        return wrapWithNamespaceDeclaration(fragment);
    }

    private String wrapFragment(String head, String fragment) {
        fragment = head + "<body><fragment>" + fragment + "</fragment></body>";
        return wrapWithNamespaceDeclaration(fragment);
    }

    private String wrapWithNamespaceDeclaration(String fragment) {
        return "<html " + namespaceDeclaration + ">"
                + fragment
                + "</html>";
    }

    public TestRig withStubbedEvaluationResult(Object evaluationResult) {
        this.evaluatorFactory = new StubEvaluator().withStubbedResult(evaluationResult);
        return this;
    }

    public TestRig withSourceFilter(String filterPrefix) {
        this.source = new FilterSource(source, filterPrefix);
        return this;
    }

    public TestRig withResource(Resource resource, String content) {
        stubSource.addResource(resource, content);
        return this;
    }

    public boolean hasCopiedResource(Resource resource) {
        return stubTarget.hasCopiedResource(resource);
    }

    public List<Resource> getCopiedResources() {
    	return stubTarget.getCopiedResources();
    }

    public TestRig withExtension(ConcordionExtension extension) {
        this.extension = extension;
        return this;
    }

    public TestRig withOutputStreamer(OutputStreamer streamer) {
        stubTarget.setOutputStreamer(streamer);
        return this;
    }

}

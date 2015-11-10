package test.concordion;

import java.io.IOException;
import java.util.List;

import org.concordion.Concordion;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Fixture;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.ClassNameBasedSpecificationLocator;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.SimpleEvaluatorFactory;
import org.concordion.internal.UnableToBuildConcordionException;
import org.concordion.internal.extension.FixtureExtensionLoader;

import spec.concordion.DummyFixture;


public class TestRig {

    private Fixture fixture;
    private EvaluatorFactory evaluatorFactory = new SimpleEvaluatorFactory();
    private StubSource stubSource = new StubSource();
    private StubTarget stubTarget;
    private FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
    private ConcordionExtension extension; 

    public TestRig withFixture(Object fixture) {
        this.fixture = new Fixture(fixture);
        return this;
    }

    public ProcessingResult processFragment(String fragment) {
        return process(wrapFragment(fragment));
    }
    
    public ProcessingResult processFragment(String resourceLocation, String head, String fragment) {
        return process(resourceLocation, wrapFragment(head, fragment));
    }

    public ProcessingResult process(Resource resource) {
        EventRecorder eventRecorder = new EventRecorder();
        stubTarget = new StubTarget();
        if (fixture == null) {
            fixture = new Fixture(new DummyFixture());
            withResource(new Resource("/spec/concordion/Dummy.html"), "<html/>");
        } else {
            withResource(new ClassNameBasedSpecificationLocator("html").locateSpecification(fixture), "<html/>");
        }
        ConcordionBuilder concordionBuilder = new ConcordionBuilder()
            .withAssertEqualsListener(eventRecorder)
            .withThrowableListener(eventRecorder)
            .withSource(stubSource)
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

            ResultSummary resultSummary = null;
            concordion.override(resource, fixture);
            List<String> examples = concordion.getExampleNames();
            if (!examples.isEmpty()) {
                for (String example : examples) {
                    resultSummary = concordion.processExample(example);
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
        return "<html xmlns:concordion='"
            + ConcordionBuilder.NAMESPACE_CONCORDION_2007 + "'>"
            + fragment
            + "</html>";
    }

    public TestRig withStubbedEvaluationResult(Object evaluationResult) {
        this.evaluatorFactory = new StubEvaluator().withStubbedResult(evaluationResult);
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
}

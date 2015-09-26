package org.concordion;

import java.io.IOException;
import java.util.List;

import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.api.Specification;
import org.concordion.api.SpecificationByExample;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.SpecificationReader;
import org.concordion.api.SpecificationToSpecificationByExampleAdaptor;
import org.concordion.internal.Fixture;
import org.concordion.internal.SummarizingResultRecorder;

public class Concordion {

    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;
    private final Resource resource;
    private final SpecificationByExample specification;
    private Fixture fixture;

    public Concordion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory, Fixture fixture) throws IOException {
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;
        this.fixture = fixture;

        resource = specificationLocator.locateSpecification(fixture);
        specification = loadSpecificationFromResource(resource);
    }

    public ResultSummary process() throws IOException {
        return process(specification, fixture);
    }

    public ResultSummary process(Resource resource, Fixture fixture) throws IOException {
        return process(loadSpecificationFromResource(resource), fixture);
    }
 
    private ResultSummary process(SpecificationByExample specification, Fixture fixture) {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        resultRecorder.setSpecificationDescription(fixture.getDescription());
        specification.process(evaluatorFactory.createEvaluator(fixture.getFixtureObject()), resultRecorder);
        return resultRecorder;
    }

    public List<String> getExampleNames() throws IOException {
        return specification.getExampleNames();
    }

    public ResultSummary processExample(String example) throws IOException {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        resultRecorder.setSpecificationDescription(example);
        specification.processExample(evaluatorFactory.createEvaluator(fixture.getFixtureObject()), example, resultRecorder);
        return resultRecorder;
    }

    /**
     * Loads the specification for the specified fixture.
     *
     * @param resource the resource to load
     * @return a SpecificationByExample object to use
     * @throws IOException if the resource cannot be loaded
     */
    private SpecificationByExample loadSpecificationFromResource(Resource resource) throws IOException {
        Specification specification= specificationReader.readSpecification(resource);

        SpecificationByExample specificationByExample;
        if (specification instanceof SpecificationByExample) {
            specificationByExample = (SpecificationByExample) specification;
        } else {
            specificationByExample = new SpecificationToSpecificationByExampleAdaptor(specification);
        }
        specificationByExample.setFixtureClass(fixture);
        return specificationByExample;
    }

    public void finish() {
        specification.finish();
    }
}

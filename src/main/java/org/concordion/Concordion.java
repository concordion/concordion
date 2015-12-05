package org.concordion;

import java.io.IOException;
import java.util.List;

import org.concordion.api.*;
import org.concordion.internal.SummarizingResultRecorder;

public class Concordion {

    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;
    private Resource resource;
    private SpecificationByExample specification;

    public Concordion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory, Fixture fixture) throws IOException {
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;

        resource = specificationLocator.locateSpecification(fixture.getFixtureObject());
    }

    /** For TestRig use only **/
    public void override(Resource resource) throws IOException {
        this.resource= resource;
    }
 
    public ResultSummary process(Fixture fixture) throws IOException {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        resultRecorder.setSpecificationDescription(fixture.getDescription());
        getSpecification(fixture).process(evaluatorFactory.createEvaluator(fixture.getFixtureObject()), resultRecorder);
        return resultRecorder;
    }

    private SpecificationByExample getSpecification(Fixture fixture) throws IOException {
        if (specification == null) {
            specification = loadSpecificationFromResource(fixture, resource);
        }
        return specification;
    }

    public List<String> getExampleNames(Fixture fixture) throws IOException {
        return getSpecification(fixture).getExampleNames();
    }

    public ResultSummary processExample(Fixture fixture, String example) throws IOException {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        resultRecorder.setSpecificationDescription(example);
        getSpecification(fixture).processExample(evaluatorFactory.createEvaluator(fixture.getFixtureObject()), example, resultRecorder);
        return resultRecorder;
    }

    /**
     * Loads the specification for the specified fixture.
     *
     * @param resource the resource to load
     * @return a SpecificationByExample object to use
     * @throws IOException if the resource cannot be loaded
     */
    private SpecificationByExample loadSpecificationFromResource(Fixture fixture, Resource resource) throws IOException {
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

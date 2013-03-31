package org.concordion;

import java.io.IOException;

import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.api.Specification;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.SpecificationReader;
import org.concordion.internal.SummarizingResultRecorder;

public class Concordion {

    private final SpecificationLocator specificationLocator;
    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;

    public Concordion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory) {
        this.specificationLocator = specificationLocator;
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;
    }

    public ResultSummary process(Object fixture) throws IOException {
        return process(specificationLocator.locateSpecification(fixture), fixture);
    }

    public ResultSummary process(Resource resource, Object fixture) throws IOException {
        Specification specification = specificationReader.readSpecification(resource);
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        specification.process(evaluatorFactory.createEvaluator(fixture), resultRecorder);
        return resultRecorder;
    }
}

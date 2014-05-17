package org.concordion;

import java.io.IOException;

import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultSummary;
import org.concordion.api.Specification;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.SpecificationReader;
import org.concordion.internal.ParsingException;
import org.concordion.internal.SummarizingResultRecorder;

public class Concordion {

    private final SpecificationLocator specificationLocator;
    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;

    public Concordion(final SpecificationLocator specificationLocator, final SpecificationReader specificationReader, final EvaluatorFactory evaluatorFactory) {
        this.specificationLocator = specificationLocator;
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;
    }

    public ResultSummary process(final Object fixture) throws IOException {
        return process(specificationLocator.locateSpecification(fixture), fixture);
    }

    public ResultSummary process(final Resource resource, final Object fixture) throws IOException {
        try {
        	final Specification specification = specificationReader.readSpecification(resource);
            final SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
            specification.process(evaluatorFactory.createEvaluator(fixture), resultRecorder);
            return resultRecorder;
        } catch (final ParsingException e) {
        	e.printStackTrace();
            final SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
            resultRecorder.record(Result.EXCEPTION);
            return resultRecorder;
       }
    }
}

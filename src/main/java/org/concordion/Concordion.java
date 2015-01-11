package org.concordion;

import java.io.IOException;
import java.util.List;

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
//        try {
        	Specification specification = loadSpecificationFromResource(resource, fixture);
            SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
            resultRecorder.setSpecificationDescription(getDefaultFixtureName(fixture));
            specification.process(evaluatorFactory.createEvaluator(fixture), resultRecorder);
            return resultRecorder;
//        } catch (ParsingException e) {
//        	e.printStackTrace();
//        	throw e;
////        	throw new ConcordionAssertionError("Could not parse resource " + resource.getPath(), new SingleResultSummary(Result.EXCEPTION));
//       }
    }

    public List<String> getExampleNames(Object fixture) throws IOException {
        Specification specification = loadSpecificationFromFixture(fixture);
        return specification.getExampleNames();
    }

    public ResultSummary processExample(Object fixture, String example) throws IOException {
        return processExample(specificationLocator.locateSpecification(fixture), fixture, example);
    }

    public ResultSummary processExample(Resource resource, Object fixture, String example) throws IOException {
//        try {
        Specification specification = loadSpecificationFromResource(resource, fixture);
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        resultRecorder.setSpecificationDescription(example);
        specification.processExample(evaluatorFactory.createEvaluator(fixture), example, resultRecorder);
        return resultRecorder;
//        } catch (ParsingException e) {
//        	e.printStackTrace();
//        	throw e;
////        	throw new ConcordionAssertionError("Could not parse resource " + resource.getPath(), new SingleResultSummary(Result.EXCEPTION));
//       }
    }


    /**
     * Loads the specification for the specified fixture.
     *
     * @param fixture the fixture for the spcification
     * @return
     * @throws IOException if the fixture's specification cannot be loaded
     */
    private Specification loadSpecificationFromFixture(Object fixture) throws IOException {
        return loadSpecificationFromResource(specificationLocator.locateSpecification(fixture), fixture);
    }

    /**
     * Loads the specification for the specified fixture.
     *
     * @param resource the resource to load
     * @return
     * @throws IOException if the resource cannot be loaded
     */
    private Specification loadSpecificationFromResource(Resource resource, Object fixture) throws IOException {
        Specification specification= specificationReader.readSpecification(resource);
        if (fixture != null) {
            specification.setFixtureClass(fixture.getClass());
        }
        return specification;
    }

    public static String getDefaultFixtureName(Object fixture) {
        Class<?> fixtureClass = fixture==null?Class.class:fixture.getClass();
        return getDefaultFixtureClassName(fixtureClass);
    }

    public static String getDefaultFixtureClassName(Class<?> fixtureClass) {
        return ("[Concordion Specification for '" + fixtureClass.getSimpleName()).replaceAll("Test$", "']"); // Based on suggestion by Danny Guerrier
    }
}

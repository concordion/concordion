package org.concordion;

import java.io.IOException;
import java.util.List;

import org.concordion.api.*;
import org.concordion.internal.FixtureType;
import org.concordion.internal.SpecificationToSpecificationByExampleAdaptor;
import org.concordion.internal.SpecificationType;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.internal.util.SimpleFormatter;

public class Concordion {

    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;
    private Resource resource;
    private SpecificationByExample specification;
    private String specificationDescription;

    /**
     * Constructor. Locates the specification with a type from the <code>specificationTypes</code> list.
     * Errors if unable to find exactly one specification of all the specified types.
     *
     * @param specificationTypes a list of types that this Concordion instance will check for (eg. html, md), with a converter for each type
     * @param specificationLocator locates the specification based on the specification type
     * @param specificationReader specification reader
     * @param evaluatorFactory evaluator factory
     * @param fixtureType
     * @throws IOException on i/o error
     */
    public Concordion(List<SpecificationType> specificationTypes, SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory, FixtureType fixtureType) throws IOException {
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;

        SpecificationType specificationType = null;

        for (SpecificationType currentType : specificationTypes) {
            Resource currentResource = specificationLocator.locateSpecification(fixtureType, currentType.getTypeSuffix());
            if (specificationReader.canFindSpecification(currentResource)) {
                if (specificationType != null) {
                    throw new RuntimeException(createMultipleSpecsMessage(fixtureType, specificationType, currentType));
                }
                specificationType = currentType;
                resource = currentResource;
            }
        }
        if (specificationType == null) {
            throw new RuntimeException(createUnableToFindSpecMessage(fixtureType, specificationTypes));
        }
        specificationReader.setSpecificationConverter(specificationType.getConverter());
    }

    /**
     * For TestRig use only
     * @param resource test resource to override the specification resource with
     * @throws IOException on i/o error
     */
    public void override(Resource resource) throws IOException {
        this.resource = resource;
    }

    public ResultSummary process(Fixture fixture) throws IOException {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        Evaluator evaluator = evaluatorFactory.createEvaluator(fixture.getFixtureObject());
        getSpecification().process(evaluator, resultRecorder, fixture);
        return resultRecorder;
    }

    private SpecificationByExample getSpecification() throws IOException {
        if (specification == null) {
            specification = loadSpecificationFromResource(resource);
            specificationDescription = specification.getDescription();
        }
        return specification;
    }

    public List<String> getExampleNames(FixtureType fixtureType) throws IOException {
        List<String> exampleNames = getSpecification().getExampleNames();
        if (exampleNames.isEmpty()) {
            exampleNames.add(fixtureType.getDescription());
        }
        return exampleNames;
    }

    public boolean hasExampleCommands() throws IOException {
        return getSpecification().hasExampleCommandNodes();
    }

    public ResultSummary processExample(Fixture fixture, String example) throws IOException {
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder(example);
        Evaluator evaluator = evaluatorFactory.createEvaluator(fixture.getFixtureObject());
        getSpecification().processExample(evaluator, example, resultRecorder, fixture);
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
        Specification specification = specificationReader.readSpecification(resource);

        SpecificationByExample specificationByExample;
        if (specification instanceof SpecificationByExample) {
            specificationByExample = (SpecificationByExample) specification;
        } else {
            specificationByExample = new SpecificationToSpecificationByExampleAdaptor(specification);
        }
        return specificationByExample;
    }

    public void finish() {
        specification.finish();
    }

    public void checkValidStatus(FixtureType fixtureType) throws IOException {
        if (hasExampleCommands() && fixtureType.getDeclaredImplementationStatus() != ImplementationStatus.EXPECTED_TO_PASS) {
            throw new IllegalStateException("Error: When the specification contains examples, "
                    + "the Implementation Status (ExpectedToFail or Unimplemented) must be set on the example command in the specification, "
                    + "and not as an annotation on the fixture.");
        }
    }

    private String createMultipleSpecsMessage(FixtureType fixtureType, SpecificationType type1, SpecificationType type2) {
        String fixturePathWithoutSuffix = fixtureType.getFixturePathWithoutSuffix();
		return SimpleFormatter.format("Found multiple matching specifications: '%s.%s' and '%s.%s'",
            fixturePathWithoutSuffix, type1.getTypeSuffix(), fixturePathWithoutSuffix, type2.getTypeSuffix());
    }

    private String createUnableToFindSpecMessage(FixtureType fixtureType, List<SpecificationType> specificationTypes) {
        String msg = "Unable to find specification: '";
        boolean first = true;
        for (SpecificationType specificationType : specificationTypes) {
            if (first) {
                first = false;
            } else {
                msg += "' or '";
            }
            msg += fixtureType.getFixturePathWithoutSuffix() + "." + specificationType.getTypeSuffix();
        }
        msg += "'";
        return msg;
    }

    public String getSpecificationDescription() {
        return specificationDescription;
    }
}

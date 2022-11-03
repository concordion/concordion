package org.concordion.integration.junit.platform.engine;

import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.FixtureType;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 * A concrete {@link FixtureBasedTestDescriptor fixture-based descriptor} for an
 * example in a specification.
 *
 * @see SpecificationDescriptor
 * @since 4.0
 */
public class ExampleDescriptor extends FixtureBasedTestDescriptor
        implements Node<ConcordionEngineExecutionContext> {

    public static final String SEGMENT_TYPE = "example";

    private final String exampleName;

    ExampleDescriptor(UniqueId parentUniqueId, Class<?> fixtureClass, String exampleName) {
        super(parentUniqueId.append(
                    ExampleDescriptor.SEGMENT_TYPE,
                    exampleName),
                fixtureClass,
                exampleName);
        this.exampleName = exampleName;
    }

    public String getExampleName() {
        return exampleName;
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }
    
    protected SpecificationDescriptor getParentSpecificationTestDescriptor() {
        TestDescriptor parentTestDescriptor = getParent().orElse(null);
        if (parentTestDescriptor == null
                || !SpecificationDescriptor.class.isInstance(parentTestDescriptor)) {
            throw new IllegalStateException(
                    "Parent must be a [" + SpecificationDescriptor.class.getName() + "]");
        }
        return (SpecificationDescriptor) parentTestDescriptor;
    }

    @Override
    public final FixtureType getFixtureType() {
        return getParentSpecificationTestDescriptor().getFixtureType();
    }

    @Override
    public final FixtureInstance getFixtureInstance() {
        return getParentSpecificationTestDescriptor().getFixtureInstance();
    }

    @Override
    public final FixtureRunner getFixtureRunner() {
        return getParentSpecificationTestDescriptor().getFixtureRunner();
    }

    /*
    @Override
    public ConcordionEngineExecutionContext before(ConcordionEngineExecutionContext context) throws Exception {
        // This is already done via FixtureRunner#run(String, Fixture)
        // getFixtureInstance().beforeExample(this.exampleName);
        return context;
    }
    */

    @Override
    public ConcordionEngineExecutionContext execute(ConcordionEngineExecutionContext context,
            DynamicTestExecutor dynamicTestExecutor) throws Exception {
        FixtureRunner runner = getFixtureRunner();

        ResultSummary result = runner.run(getExampleName(), getFixtureInstance());
        result.assertIsSatisfied(getFixtureType());

        return context;
    }

    /*
    @Override
    public void after(ConcordionEngineExecutionContext context) throws Exception {
        // This is already done via FixtureRunner#run(String, Fixture)
        // getFixtureInstance().afterExample(this.exampleName);
    }
    */

}

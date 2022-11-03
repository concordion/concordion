package org.concordion.integration.junit.platform.engine;

import java.io.IOException;
import java.util.List;

import org.concordion.api.SpecificationLocator;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.FixtureType;
import org.concordion.internal.RunOutput;
import org.concordion.internal.UnableToBuildConcordionException;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 * A concrete {@link FixtureBasedTestDescriptor fixture-based descriptor} for a
 * specification.
 *
 * @see ExampleDescriptor
 * @since 4.0
 */
public class SpecificationDescriptor extends FixtureBasedTestDescriptor
        implements Node<ConcordionEngineExecutionContext> {

    public static final String SEGMENT_TYPE = "specification";

    private final SpecificationLocator specificationLocator;

    private FixtureType fixtureType;
    private Object fixtureObject;
    private FixtureInstance fixtureInstance;
    private FixtureRunner fixtureRunner;

    SpecificationDescriptor(UniqueId parentUniqueId,
            Class<?> fixtureClass, SpecificationLocator specificationLocator) {
        super(parentUniqueId.append(
                    SpecificationDescriptor.SEGMENT_TYPE, fixtureClass.getName()),
                fixtureClass);
        this.specificationLocator = specificationLocator;
    }

    public List<String> getExampleNames() throws IOException {
        return getFixtureRunner().getConcordion().getExampleNames(getFixtureType());
    }

    @Override
    public FixtureType getFixtureType() {
        if (fixtureType == null) {
            fixtureType = getFixtureInstance().getFixtureType();
        }
        return fixtureType;
    }

    @Override
    public FixtureInstance getFixtureInstance() {
        if (fixtureInstance == null) {
            fixtureInstance = createFixtureInstance();
        }
        return fixtureInstance;
    }

    protected FixtureInstance createFixtureInstance() {
        return new FixtureInstance(createFixtureObject());
    }

    protected Object createFixtureObject() {
        if (fixtureObject == null) {
            fixtureObject = ReflectionSupport.newInstance(getFixtureClass());
        }
        return fixtureObject;
    }
    
    public FixtureRunner getFixtureRunner() {
        if (fixtureRunner == null) {
            try {
                fixtureRunner = createFixtureRunner();
            } catch (UnableToBuildConcordionException ex) {
                throw new RuntimeException(ex);
            }
        }
        return fixtureRunner;
    }

    protected FixtureRunner createFixtureRunner() throws UnableToBuildConcordionException {
        return new FixtureRunner(getFixtureInstance(), specificationLocator);
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public ConcordionEngineExecutionContext prepare(ConcordionEngineExecutionContext context) throws Exception {
        FixtureRunner runner = getFixtureRunner(); // create FixtureRunner
        // (which can result into a RuntimeException with a nested UnableToBuildConcordionException)
        runner.getConcordion().checkValidStatus(getFixtureType());
        // Child ExampleDescriptor(s) shall access the FixtureRunner via getFixtureRunner()
        return context;
    }
    
    @Override
    public void around(ConcordionEngineExecutionContext context,
            Invocation<ConcordionEngineExecutionContext> invocation) throws Exception {
        boolean isFirstRun = null == context.getRunResultsCache().getFromCache(getFixtureClass(), null);
        
        if (isFirstRun) {
            context.getRunResultsCache().startFixtureRun(
                    getFixtureType(),
                    getFixtureRunner().getConcordion().getSpecificationDescription());
            // getFixtureInstance().beforeSuite();
            getFixtureInstance().beforeSpecification();
        }

        Node.super.around(context, invocation);

        if (isFirstRun) {
            getFixtureInstance().afterSpecification();
            getFixtureRunner().getConcordion().finish();
            // getFixtureInstance().afterSuite();
        }

        RunOutput results = context.getRunResultsCache().getFromCache(getFixtureClass(), null);
        if (results != null) {
            synchronized (System.out) {
                results.getActualResultSummary().print(System.out, getFixtureType());
            }
        }
    }

}

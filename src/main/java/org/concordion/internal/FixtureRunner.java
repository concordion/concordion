package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.ResultSummary;
import org.concordion.internal.cache.RunResultsCache;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.concordion.internal.extension.FixtureExtensionLoader;

import java.io.IOException;

public class FixtureRunner {
    private static RunResultsCache runResultsCache = RunResultsCache.SINGLETON;
    private final Fixture fixture;

    public FixtureRunner(Fixture fixture) throws UnableToBuildConcordionException {
        this.fixture = fixture;

        ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
        concordion = concordionBuilder.build();

    }

    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
    private Concordion concordion;

    public ResultSummary run(String example) throws IOException {
    	
    	ConcordionRunOutput runOutput = runResultsCache.startRun(fixture, example);
        ResultSummary actualResultSummary = runOutput==null?
                null:
                runOutput.getActualResultSummary();

        ResultSummary postProcessedResultSummary = runOutput==null?
                null:
                runOutput.getModifiedResultSummary();


        String additionalInformation = null;
    	if (runOutput == null) {

            try {
                ExpectedState state;
                if (example != null) {
                    actualResultSummary = concordion.processExample(example);
                    state = ExpectedState.getExpectedState(
                            fixture.getFixtureClass(),
                            fixture, actualResultSummary.getResultModifier());
                } else {
                    actualResultSummary = concordion.process();
                    state = ExpectedState.getExpectedState(
                            fixture.getFixtureClass(),
                            fixture, null);
                }
                // we want to make sure all the annotations are considered when storing the result summary
                // converting for the cache doesn't need the example - it just does annotation based conversions
                postProcessedResultSummary=  state.convertForCache(actualResultSummary);

                runResultsCache.finishRun(fixture,
                        example,
                        actualResultSummary,
                        postProcessedResultSummary);

            } catch (RuntimeException e) {
                // the run failed miserably. Tell the cache that the run failed
                runResultsCache.failRun(fixture, example);
                throw e;
            }

        } else {
            additionalInformation = "\nFrom cache: ";
        }

        printResultSummary(fixture, example, actualResultSummary, additionalInformation);

        return actualResultSummary;
    }

    private void printResultSummary(Fixture fixture, String example, ResultSummary resultSummary, String additionalInformation) {
        synchronized (System.out) {
            if (additionalInformation != null) {
                System.out.print(additionalInformation);
            }
            resultSummary.print(System.out, fixture);
            resultSummary.assertIsSatisfied(fixture);
        }
    }

    public synchronized Concordion getConcordion() {
            return concordion;
    }

    public ResultSummary run() throws IOException {
        return run(null);
    }
}
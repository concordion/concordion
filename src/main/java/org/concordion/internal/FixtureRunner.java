package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.Fixture;
import org.concordion.api.ResultSummary;
import org.concordion.internal.cache.RunResultsCache;
import org.concordion.internal.cache.ConcordionRunOutput;
import org.concordion.internal.extension.FixtureExtensionLoader;

import java.io.IOException;

public class FixtureRunner {
    private static RunResultsCache runResultsCache = RunResultsCache.SINGLETON;
    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
    private final FixtureOptionsLoader fixtureOptionsLoader = new FixtureOptionsLoader();
    private Concordion concordion;

    public FixtureRunner(Fixture fixture) throws UnableToBuildConcordionException {
        ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
        fixtureOptionsLoader.addOptions(fixture, concordionBuilder);
        concordion = concordionBuilder.build();
    }

    public ResultSummary run(String example, Fixture fixture) throws IOException {

    	ConcordionRunOutput runOutput = runResultsCache.startRun(fixture, example);
        ResultSummary actualResultSummary = runOutput==null?
                null:
                runOutput.getActualResultSummary();

        String additionalInformation = null;
    	if (runOutput == null) {

            try {
                ImplementationStatusChecker statusChecker;
                if (example != null) {
                    fixture.beforeExample(example);
                    try {
                        actualResultSummary = concordion.processExample(fixture, example);
                        statusChecker = ImplementationStatusChecker.getImplementationStatusChecker(
                                fixture,
                                actualResultSummary.getImplementationStatus());
                    } finally {
                        fixture.afterExample(example);
                    }
                } else {
                    actualResultSummary = concordion.process(fixture);
                    statusChecker = ImplementationStatusChecker.getImplementationStatusChecker(
                            fixture,
                            null);
                }

                runResultsCache.finishRun(fixture,
                        example,
                        actualResultSummary,
                        statusChecker);

            } catch (RuntimeException e) {
                // the run failed miserably. Tell the cache that the run failed
                runResultsCache.failRun(fixture, example);
                throw e;
            }

        } else {
            additionalInformation = "\nFrom cache: ";
        }

        if (actualResultSummary.isForExample()) {
            printResultSummary(fixture, example, actualResultSummary, additionalInformation);
        }

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

    /**
     *
     * This method is deprecated as it's only used in jUnit 3.
     *
     * @param fixture fixture
     * @return result summary
     * @throws IOException on io error
     */
    @Deprecated
    public ResultSummary run(Fixture fixture) throws IOException {
        ConcordionRunOutput results = RunResultsCache.SINGLETON.getFromCache(fixture, null);

        ResultSummary resultSummary = run(null, fixture);

        // only actually finish the specification if it has not already been run.
        if (results == null) {
            concordion.finish();
        }
        return resultSummary;
    }
}

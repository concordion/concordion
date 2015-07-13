package org.concordion.internal;

import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.internal.extension.FixtureExtensionLoader;

public class FixtureRunner {
	private static CachedRunResults cachedRunResults = CachedRunResults.SINGLETON;
	
    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();

    public ResultSummary run(Object fixture) throws IOException {
    	
        ResultSummary actualResultSummary = null; 
        String additionalInformation = null;
        ConcordionRunOutput cachedRunOutput = cachedRunResults.startRun(fixture.getClass());
        
    	if (cachedRunOutput == null) {
            ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
            fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);

            try {
                actualResultSummary = concordionBuilder.build().process(fixture);
                // we want to make sure all the annotations are considered when storing the result summary
                ResultSummary postProcessedResultSummary = cachedRunResults.convertForCache(actualResultSummary, fixture.getClass());

                cachedRunResults.finishRun(fixture.getClass(),
                        actualResultSummary,
                        postProcessedResultSummary);

            } catch (RuntimeException e) {
                // the run failed miserably. Tell the cache that the run failed
                cachedRunResults.failRun(fixture.getClass());
                throw e;
            }

        } else {
            actualResultSummary = cachedRunOutput.getActualResultSummary();
            additionalInformation = "From cache: ";
        }

        printResultSummary(fixture, actualResultSummary, additionalInformation);

        return actualResultSummary.getMeaningfulResultSummary(fixture);
    }

    private void printResultSummary(Object fixture, ResultSummary resultSummary, String additionalInformation) {
        synchronized (System.out) {
            if (additionalInformation != null) {
                System.out.print(additionalInformation);
            }
            resultSummary.print(System.out, fixture);
            resultSummary.assertIsSatisfied(fixture);
        }
    }
}
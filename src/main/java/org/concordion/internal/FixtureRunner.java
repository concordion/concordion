package org.concordion.internal;

import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.internal.extension.FixtureExtensionLoader;

public class FixtureRunner {
	private static CachedRunResults cachedRunResults = CachedRunResults.SINGLETON;
	
    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();

    public ResultSummary run(Object fixture) throws IOException {
    	
    	ResultSummary resultSummary = cachedRunResults.startRun(fixture.getClass());
        String additionalInformation = null;
    	if (resultSummary == null)  {
            ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
            fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
            resultSummary = concordionBuilder.build().process(fixture);

            // we want to make sure all the annotations are considered when storing the result summary
            cachedRunResults.finishRun(fixture.getClass(), resultSummary.convertForCache(fixture));
        } else {
            additionalInformation = "Returning cached result summary for fixture " + fixture.getClass().getName();
        }

        synchronized (System.out) {
            if (additionalInformation != null) {
                System.out.println(additionalInformation);
            }
            resultSummary.print(System.out, fixture);
            resultSummary.assertIsSatisfied(fixture);
        }

        return resultSummary.getMeaningfulResultSummary(fixture);
    }
}
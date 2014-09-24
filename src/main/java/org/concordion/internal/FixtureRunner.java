package org.concordion.internal;

import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.internal.extension.FixtureExtensionLoader;

public class FixtureRunner {
	
	private static CachedRunResults cachedRunResults = new CachedRunResults();

	
    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();

    public ResultSummary run(Object fixture) throws IOException {
    	
    	ResultSummary resultSummary = cachedRunResults.getCachedResults(fixture.getClass());
        String additionalInformation = null;
    	if (resultSummary == null)  {
            ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
            fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
            resultSummary = concordionBuilder.build().process(fixture);
            cachedRunResults.addResultSummary(fixture.getClass(), resultSummary);
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
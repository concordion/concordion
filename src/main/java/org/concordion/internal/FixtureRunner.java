package org.concordion.internal;

import java.io.IOException;

import org.concordion.Concordion;
import org.concordion.api.ResultSummary;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.internal.extension.FixtureExtensionLoader;

public class FixtureRunner {
	private static CachedRunResults cachedRunResults = new CachedRunResults();
    private final Object fixture;

    public FixtureRunner(Object fixture) {
        this.fixture = fixture;

        ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
        concordion = concordionBuilder.build();

    }

    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
    private Concordion concordion;

    public ResultSummary run(String example) throws IOException {
    	
    	ResultSummary resultSummary = cachedRunResults.startRun(fixture.getClass(), example);
        String additionalInformation = null;

    	if (resultSummary == null)  {

            if (example != null) {
                resultSummary = concordion.processExample(fixture, example);
            } else {
                resultSummary = concordion.process(fixture);
            }
            cachedRunResults.finishRun(fixture.getClass(), example, resultSummary);
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

    public synchronized Concordion getConcordion() {
            return concordion;
    }

    public ResultSummary run() throws IOException {
        return run(null);
    }
}
package org.concordion.internal;

import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.internal.extension.FixtureExtensionLoader;

public class FixtureRunner {
    private final FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();

    public ResultSummary run(Object fixture) throws IOException {
        ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
        ResultSummary resultSummary = concordionBuilder.build().process(fixture);
        synchronized (System.out) {
            resultSummary.print(System.out, fixture);
            resultSummary.assertIsSatisfied(fixture);
        }
        return resultSummary.getMeaningfulResultSummary(fixture);
    }
}
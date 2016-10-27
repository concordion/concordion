package spec.concordion.results.runTotals;

import java.util.Map;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

@RunWith(ConcordionRunner.class)
public class RunTotalsFixture {

	private Class<?> testClass;

	public RunTotalsFixture() {
		withTestClass(getClass());
	}

	public RunTotalsFixture withTestClass(Class<?> fixtureClass) {
		this.testClass = fixtureClass;
		return this;
	}

	public Map<String, String> simulateRun(final String href) throws Exception {
		return new RunCommandSimulator().simulate(href, testClass);
	}
}

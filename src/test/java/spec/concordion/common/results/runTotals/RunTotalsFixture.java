package spec.concordion.common.results.runTotals;

import java.util.Map;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.Fixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FixtureInstance;
import org.junit.runner.RunWith;
import test.concordion.RunCommandSimulator;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class RunTotalsFixture {

	private Class<?> testClass;
	private Fixture fixture;

	public RunTotalsFixture() {
		withTestClass(getClass());
		withFixture(new FixtureInstance(this));
	}

	private RunTotalsFixture withFixture(Fixture fixture) {
		this.fixture = fixture;
		return this;
	}

	public RunTotalsFixture withTestClass(Class<?> fixtureClass) {
		this.testClass = fixtureClass;
		return this;
	}

	public Map<String, Object> simulateRun(final String href) throws Exception {
		return new RunCommandSimulator().simulate(href, testClass);
	}
}

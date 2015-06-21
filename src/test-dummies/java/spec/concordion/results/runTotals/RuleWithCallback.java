package spec.concordion.results.runTotals;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by tim on 20/06/15.
 */
class RuleWithCallback implements TestRule {
    private final Statement callback;

    public RuleWithCallback(Statement callback) {
        this.callback = callback;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        // pass the callback object first.
        return new CompoundStatement(callback, base);
    }

}

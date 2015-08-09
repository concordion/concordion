package spec.concordion.integration.junit4;

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

    public Statement apply(Statement base, Description description) {
        // pass the callback object first.
        return new CompoundStatement(callback, base);
    }

}

package spec.concordion.common.results.runTotals;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RuleWithCallback implements TestRule {

    private static class CompoundStatement extends Statement {
        Statement statement1;
        Statement statement2;

        public CompoundStatement(Statement statement1, Statement statement2) {
            this.statement1 = statement1;
            this.statement2 = statement2;
        }

        @Override
        public void evaluate() throws Throwable {
            statement1.evaluate();
            statement2.evaluate();
        }
    }


    private final Statement callback;

    public RuleWithCallback(Statement callback) {
        this.callback = callback;
    }

    public Statement apply(Statement base, Description description) {
        // pass the callback object first.
        return new CompoundStatement(callback, base);
    }

}

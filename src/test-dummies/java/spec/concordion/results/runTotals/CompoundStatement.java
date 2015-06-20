package spec.concordion.results.runTotals;

import org.junit.runners.model.Statement;

/**
 * Created by tim on 20/06/15.
 */
class CompoundStatement extends Statement {
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

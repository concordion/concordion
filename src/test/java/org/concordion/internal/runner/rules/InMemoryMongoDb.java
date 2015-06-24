package org.concordion.internal.runner.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by tim on 24/06/15.
 */
public class InMemoryMongoDb implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return base;
    }
}

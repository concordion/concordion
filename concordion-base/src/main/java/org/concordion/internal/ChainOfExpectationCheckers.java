package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

public class ChainOfExpectationCheckers implements ExpectationChecker {

    private List<ExpectationChecker> checkers = new ArrayList<ExpectationChecker>();

    public ChainOfExpectationCheckers add(ExpectationChecker checker) {
        checkers.add(checker);
        return this;
    }
    
    public boolean isAcceptable(Object actual, String expected) {
        for (ExpectationChecker checker : checkers) {
            if (checker.isAcceptable(actual, expected)) {
                return true;
            }
        }
        return false;
    }
}

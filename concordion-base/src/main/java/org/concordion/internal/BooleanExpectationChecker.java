package org.concordion.internal;


public class BooleanExpectationChecker implements ExpectationChecker {

    public boolean isAcceptable(Object actual, String expected) {
        if (!(actual instanceof Boolean)) {
            return false;
        }
        boolean b = (Boolean) actual;
        String s = CatchAllExpectationChecker.normalize(expected).toLowerCase();
        
        return (b && s.matches("(true|yes|y)")) || (!b && s.matches("(false|no|n)"));
    }
}

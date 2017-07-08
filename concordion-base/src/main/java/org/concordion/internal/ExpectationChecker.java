package org.concordion.internal;

public interface ExpectationChecker {
    
    boolean isAcceptable(Object actual, String expected);
}


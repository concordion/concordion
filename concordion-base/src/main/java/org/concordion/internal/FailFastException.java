package org.concordion.internal;

public class FailFastException extends RuntimeException {

    private static final long serialVersionUID = -6486883059833084214L;
    
    public FailFastException(String message, Throwable t) {
        super(message, t);
    }
}


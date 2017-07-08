package org.concordion.internal;

public class InvalidExpressionException extends RuntimeException {

    private static final long serialVersionUID = 4476243661580478981L;
    private final Throwable t;

    public InvalidExpressionException(String message, Throwable t) {
        super(message, t);
        this.t = t;
    }
    
    public InvalidExpressionException(String message) {
        this(message, null);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        if (t == null) {
            return super.getStackTrace();
        }
        return t.getStackTrace();
    }
}

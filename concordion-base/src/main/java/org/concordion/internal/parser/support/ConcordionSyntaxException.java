package org.concordion.internal.parser.support;

public class ConcordionSyntaxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConcordionSyntaxException() {
    }

    public ConcordionSyntaxException(String message) {
        super(message);
    }

    public ConcordionSyntaxException(Throwable cause) {
        super(cause);
    }

    public ConcordionSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}

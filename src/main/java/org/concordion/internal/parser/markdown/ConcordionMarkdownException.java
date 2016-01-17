package org.concordion.internal.parser.markdown;

public class ConcordionMarkdownException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConcordionMarkdownException() {
    }

    public ConcordionMarkdownException(String message) {
        super(message);
    }

    public ConcordionMarkdownException(Throwable cause) {
        super(cause);
    }

    public ConcordionMarkdownException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.concordion.internal.parser;

/**
 * Thrown by Concordion to indicate that an error initialising a Parser (eg Flexmark).
 */
public class ParserInitialisationException extends RuntimeException {

    private static final long serialVersionUID = -6845202643990333414L;

    public ParserInitialisationException() {
        super();
    }

    public ParserInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserInitialisationException(String message) {
        super(message);
    }

    public ParserInitialisationException(Throwable cause) {
        super(cause);
    }
}

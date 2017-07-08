package org.concordion.internal;

public class ParsingException extends RuntimeException {

    private static final long serialVersionUID = 940993549779215305L;
    private final String sourceDescription;

    public ParsingException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ParsingException(String message, Throwable cause, String sourceDescription) {
        super(message, cause);
        this.sourceDescription = sourceDescription;
    }

    public String getMessage() {
        String message = super.getMessage();
        if (sourceDescription != null) {
            message += " " + sourceDescription;
        }
        return message;
    }
}

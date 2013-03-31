package org.concordion.internal.extension;

/**
 * Thrown by Concordion to indicate that an extension could not be initialised.
 */
public class ExtensionInitialisationException extends RuntimeException {

    private static final long serialVersionUID = -6845202643990333414L;

    public ExtensionInitialisationException() {
        super();
    }

    public ExtensionInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionInitialisationException(String message) {
        super(message);
    }

    public ExtensionInitialisationException(Throwable cause) {
        super(cause);
    }
}

package org.concordion.internal;

import java.io.IOException;

public class UnableToBuildConcordionException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnableToBuildConcordionException(IOException e) {
        super(e);
    }
}

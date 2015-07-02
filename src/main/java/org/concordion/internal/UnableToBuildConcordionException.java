package org.concordion.internal;

import java.io.IOException;

/**
 * Created by tim on 2/07/15.
 */
public class UnableToBuildConcordionException extends Exception {
    public UnableToBuildConcordionException(IOException e) {
        super(e);
    }
}

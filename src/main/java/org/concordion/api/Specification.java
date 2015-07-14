package org.concordion.api;

import java.util.List;

/**
 *
 * This interface changed in concordion 1.6.0 - several new methods were implemented. Default versions of these methods
 * that mirror existing functionality are available by extending AbstractSpecification - instead of implementing
 * Specification.
 *
 */
public interface Specification {

    void process(Evaluator evaluator, ResultRecorder resultRecorder);

}

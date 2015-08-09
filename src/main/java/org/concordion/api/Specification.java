package org.concordion.api;

/**
 *
 * This interface changed in Concordion 2.0 - several new methods were implemented. Default versions of these methods
 * that mirror existing functionality are available by extending AbstractSpecification - instead of implementing
 * Specification.
 *
 */
public interface Specification {

    void process(Evaluator evaluator, ResultRecorder resultRecorder);

}

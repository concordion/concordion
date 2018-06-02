package org.concordion.api;

public interface Specification {

    void process(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture);

    /**
     * Gets the description of the exported specification.
     *
     * @return specification description
     */
    String getDescription();
}

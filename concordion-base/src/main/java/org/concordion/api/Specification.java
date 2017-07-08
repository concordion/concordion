package org.concordion.api;

public interface Specification {

    void process(Evaluator evaluator, ResultRecorder resultRecorder);
}

package org.concordion.api;

import java.util.List;

/**
 *
 * This interface changed in concordion 2.0 - several new methods were implemented. Default versions of these methods
 * that mirror existing functionality are available by extending AbstractSpecification - instead of implementing
 * Specification.
 *
 */
public interface SpecificationByExample extends Specification {

	/*
	 * Sets the fixture class. Will be called before the other methods are called so that
	 * the class can process the fixture to determine examples, etc.
	 */
    void setFixture(Fixture fixture);


    /**
     * 
     * Gets all the examples in the specification
     * 
     * @return
     */
    List<String> getExampleNames();
    
    /**
     * 
     * Processes a single example
     * 
     * @param evaluator
     * @param example
     * @param resultRecorder
     */
    void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder);

    /**
     * 
     * Called once all examples have been executed so the spec can do things like "save HTML results to file"
     * 
     */
    void finish();
}

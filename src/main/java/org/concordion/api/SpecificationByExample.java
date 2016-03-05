package org.concordion.api;

import java.util.List;

import org.concordion.internal.SummarizingResultRecorder;

/**
 * Extension interface for {@link Specification}s that contain Concordion examples.
 * 
 * @since 2.0.0
 */
public interface SpecificationByExample extends Specification {

	/**
	 * Sets the fixture. Will be called before the other methods are called so that
	 * the class can process the fixture to determine examples, etc.
	 * 
	 * @param fixture the fixture instance
	 */
    void setFixture(Fixture fixture);


    /**
     * Gets all the examples in the specification.
     * 
     * @return names of the examples
     */
    List<String> getExampleNames();
    
    /**
     * Processes a single example.
     * 
     * @param evaluator evaluator
     * @param example name of the example
     * @param resultRecorder result recorder
     */
    void processExample(Evaluator evaluator, String example, SummarizingResultRecorder resultRecorder);

    /**
     * Called once all examples have been executed so the spec can do things like "save HTML results to file".
     */
    void finish();
}

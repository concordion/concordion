package org.concordion.api;

import org.concordion.Concordion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * Created by tim on 9/01/15.
 */
public class SpecificationToSpecificationByExampleAdaptor implements SpecificationByExample {

    private String testDescription;
    private final Specification specification;

    public SpecificationToSpecificationByExampleAdaptor(Specification s) {
        specification = s;
    }
    
    public void finish() {
    }

    public void process(Evaluator evaluator, ResultRecorder resultRecorder) {
        specification.process(evaluator, resultRecorder);
    }

    public void setFixtureClass(Class<?> fixture) {
        testDescription = Concordion.getDefaultFixtureClassName(fixture);
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            specification.process(evaluator, resultRecorder);
        }
    }

    public List<String> getExampleNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(testDescription);
        return list;
    }
}

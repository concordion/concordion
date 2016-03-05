package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;

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

    public void setFixture(Fixture fixture) {
        testDescription = fixture.getSpecificationDescription();
    }

    public void processExample(Evaluator evaluator, String example, SummarizingResultRecorder resultRecorder) {
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

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

    public void process(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        specification.process(evaluator, resultRecorder, fixture);
    }

    public void setFixture(Fixture fixture) {
        testDescription = fixture.getSpecificationDescription();
    }

    public void processExample(Fixture fixture, Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            specification.process(evaluator, resultRecorder, fixture);
        }
    }

    public List<String> getExampleNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(testDescription);
        return list;
    }

    @Override
    public boolean hasExampleCommandNodes() {
        return false;
    }

    @Override
    public String getSpecificationDescription() {
        return testDescription;
    }
}

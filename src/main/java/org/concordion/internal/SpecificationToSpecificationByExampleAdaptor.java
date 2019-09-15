package org.concordion.internal;

import java.util.Collections;
import java.util.List;

import org.concordion.api.*;

public class SpecificationToSpecificationByExampleAdaptor implements SpecificationByExample {

    private final Specification specification;

    public SpecificationToSpecificationByExampleAdaptor(Specification specification) {
        this.specification = specification;
    }

    public void finish() {
    }

    public void process(Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        specification.process(evaluator, resultRecorder, fixture);
    }

    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder, Fixture fixture) {
    }

    public List<String> getExampleNames() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasExampleCommandNodes() {
        return false;
    }

    @Override
    public String getDescription() {
        return specification.getDescription();
    }
}

package org.concordion.api;

import com.google.common.collect.Lists;
import org.concordion.Concordion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * Created by tim on 9/01/15.
 */
public abstract class AbstractSpecification implements Specification {

    private String testDescription;

    public AbstractSpecification() {
        setFixtureClass(Object.class);
    }

    @Override
    public void setFixtureClass(Class<?> fixture) {
        testDescription = Concordion.getDefaultFixtureClassName(fixture);
    }

    @Override
    public void processExample(Evaluator evaluator, String example, ResultRecorder resultRecorder) {
        if (testDescription.equals(example)) {
            process(evaluator, resultRecorder);
        }
    }

    @Override
    public List<String> getExampleNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(testDescription);
        return list;
    }
}

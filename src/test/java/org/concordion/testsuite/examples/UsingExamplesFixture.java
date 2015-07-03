package org.concordion.testsuite.examples;

import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

/**
 * Created by tim on 2/07/15.
 */
@RunWith(ConcordionRunner.class)
@FullOGNL
public class UsingExamplesFixture {

    private int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void setCounter(String value) {
        this.counter = Integer.parseInt(value);
    }

    public void incrementCounter() {
        counter++;
    }

    public boolean isTrue() {
        return true;
    }

    public boolean isNull(String object) {
        return object == null;
    }

}

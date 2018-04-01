package org.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class BeforeExample {
    private String VBTest;

    @org.concordion.api.BeforeExample
    public void init() {
        System.out.println("init");
        VBTest = "Tony";
    }

    public int getSum(int a, int b) {
        System.out.println(VBTest);
        return a + b;
    }

    public int getQuotient(int a, int b) {
        System.out.println(VBTest);
        return a / b;
    }
}

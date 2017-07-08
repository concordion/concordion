package org.concordion.internal;

import java.util.Comparator;
import org.concordion.internal.util.Check;

public class BrowserStyleWhitespaceComparator implements Comparator<Object> {

    private ChainOfExpectationCheckers chainOfCheckers = new ChainOfExpectationCheckers();
    
    public BrowserStyleWhitespaceComparator() {
        chainOfCheckers.add(new BooleanExpectationChecker());
        chainOfCheckers.add(new CatchAllExpectationChecker());
    }
    
    public int compare(Object o1, Object o2) {
        Check.isTrue(o2 instanceof String, "This comparator only supports comparisons with String objects");
        if (chainOfCheckers.isAcceptable(o1, (String) o2)) {
            return 0;
        }
        return -1;
    }
}

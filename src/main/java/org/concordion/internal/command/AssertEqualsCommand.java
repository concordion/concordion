package org.concordion.internal.command;

import java.util.Comparator;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.BrowserStyleWhitespaceComparator;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class AssertEqualsCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    private final Comparator<Object> comparator;

    public AssertEqualsCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertEqualsCommand(Comparator<Object> comparator) {
        this.comparator = comparator;
    }
    
    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        ComparisonResult result = doCompare(commandCall, evaluator);

        if (result.equals) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(result.element);
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(result.element, result.expected, result.actual);
        }
    }

    @Override
    public Result verifyInBackground(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        return doCompare(commandCall, evaluator).equals
                ? Result.SUCCESS
                : Result.FAILURE;
    }

    private ComparisonResult doCompare(CommandCall commandCall, Evaluator evaluator) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertEquals' is not supported");

        Element element = commandCall.getElement();

        Object actual = evaluator.evaluate(commandCall.getExpression());
        String expected = element.getText();

        return new ComparisonResult(comparator.compare(actual, expected) == 0, actual, expected, element);
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }

    private static final class ComparisonResult {

        private final boolean equals;
        private final Object actual;
        private final String expected;
        private final Element element;

        public ComparisonResult(boolean equals, Object actual, String expected, Element element ) {
            this.equals = equals;
            this.actual = actual;
            this.expected = expected;
            this.element = element;
        }
    }
}

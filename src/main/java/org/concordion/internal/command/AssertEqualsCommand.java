package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import org.concordion.internal.util.Check;

public class AssertEqualsCommand extends AbstractCommand {

    private List<AssertEqualsListener> listeners = new ArrayList<AssertEqualsListener>();
    private final Comparator<Object> comparator;

    public AssertEqualsCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertEqualsCommand(Comparator<Object> comparator) {
        this.comparator = comparator;
    }
    
    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.add(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertEquals' is not supported");
        
        Element element = commandCall.getElement();
        
        Object actual = evaluator.evaluate(commandCall.getExpression());
        String expected = element.getText();
        
        if (comparator.compare(actual, expected) == 0) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, actual);
        }
    }
    
    private void announceSuccess(Element element) {
        for (AssertEqualsListener listener : listeners) {
			listener.successReported(new AssertSuccessEvent(element));
		}
    }

    private void announceFailure(Element element, String expected, Object actual) {
        for (AssertEqualsListener listener : listeners) {
			listener.failureReported(new AssertFailureEvent(element, expected, actual));
		}
    }
}

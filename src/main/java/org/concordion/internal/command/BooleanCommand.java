package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertListener;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.InvalidExpressionException;

public abstract class BooleanCommand extends AbstractCommand {

    private List<AssertListener> listeners = new ArrayList<AssertListener>();
    
    public void addAssertListener(AssertListener listener) {
        listeners.add(listener);
    }

    public void removeAssertListener(AssertListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
//        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertTrue' is not supported");
        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);
        
        String expression = commandCall.getExpression();
        Object result = evaluator.evaluate(expression);
        if (result != null && result instanceof Boolean) {
            if ((Boolean) result) {
                processTrueResult(commandCall, resultRecorder);
            } else {
                processFalseResult(commandCall, resultRecorder);
            }
        } else {
            throw new InvalidExpressionException("Expression '" + expression + "' did not produce a boolean result (needed for assertTrue).");
        }
    }
    
    protected void announceSuccess(Element element) {
        for (AssertListener listener : listeners) {
			listener.successReported(new AssertSuccessEvent(element));
		}
    }

    protected void announceFailure(Element element, String expected, Object actual) {
        for (AssertListener listener : listeners) {
			listener.failureReported(new AssertFailureEvent(element, expected, actual));
		}
    }
    
    protected abstract void processTrueResult(CommandCall commandCall,ResultRecorder resultRecorder);
    protected abstract void processFalseResult(CommandCall commandCall, ResultRecorder resultsRecorder);
}

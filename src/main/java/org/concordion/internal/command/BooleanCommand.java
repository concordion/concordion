package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertListener;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.InvalidExpressionException;
import org.concordion.internal.util.Announcer;

public abstract class BooleanCommand extends AbstractCommand {

    private Announcer<AssertListener> listeners = Announcer.to(AssertListener.class);
    
    public void addAssertListener(AssertListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertListener(AssertListener listener) {
        listeners.removeListener(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        if (doVerify(commandCall, evaluator, resultRecorder)) {
            processTrueResult(commandCall, resultRecorder);
        } else {
            processFalseResult(commandCall, resultRecorder);
        }
    }

    protected final Boolean doVerify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
//        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertTrue' is not supported");
        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);

        String expression = commandCall.getExpression();
        Object result = evaluator.evaluate(expression);
        if (result != null && result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new InvalidExpressionException("Expression '" + expression + "' did not produce a boolean result (needed for assertTrue).");
        }
    }

    protected void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    protected void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
    
    protected abstract void processTrueResult(CommandCall commandCall,ResultRecorder resultRecorder);
    protected abstract void processFalseResult(CommandCall commandCall, ResultRecorder resultsRecorder);
}

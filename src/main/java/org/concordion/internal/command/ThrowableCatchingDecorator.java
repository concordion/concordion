package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.internal.FailFastException;

public class ThrowableCatchingDecorator extends AbstractCommandDecorator {

    private final List<ThrowableCaughtListener> listeners = new ArrayList<ThrowableCaughtListener>();
    private final List<Class<? extends Throwable>> failFastExceptions;
    
    public void addThrowableListener(ThrowableCaughtListener listener) {
        listeners.add(listener);
    }

    public void removeThrowableListener(ThrowableCaughtListener listener) {
        listeners.remove(listener);
    }
    
    public ThrowableCatchingDecorator(Command command, List<Class<? extends Throwable>> failFastExceptions) {
        super(command);
        this.failFastExceptions = failFastExceptions;
    }

    private void announceThrowableCaught(Element element, Throwable t, String expression) {
    	for (ThrowableCaughtListener listener : listeners) {
    		listener.throwableCaught(new ThrowableCaughtEvent(t, element, expression));
		}
    }

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        try {
            runnable.run();
        } catch (FailFastException ffe) {
            // We get here if a sub-fixture has thrown an exception and we want the fail fast exception to percolate up.
            rethrowFailFastException(resultRecorder, ffe.getCause());
        } catch (Throwable t) {
            resultRecorder.record(Result.EXCEPTION);
            announceThrowableCaught(commandCall.getElement(), t, commandCall.getExpression());
            rethrowFailFastException(resultRecorder, t);
        }        
    }

    private void rethrowFailFastException(ResultRecorder resultRecorder, Throwable t) {
        for (Class<? extends Throwable> exceptionType : failFastExceptions) {

            Throwable cause = t.getCause() == null? t:t.getCause();

            if (exceptionType.isAssignableFrom(cause.getClass())) {
                FailFastException failFastException = new FailFastException("An exception was thrown in a @FailFast fixture", t);
                resultRecorder.recordFailFastException(failFastException);
                throw failFastException;
            }
        }
    }
}

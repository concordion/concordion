package org.concordion.internal.command;

import java.util.List;

import org.concordion.api.AbstractCommandDecorator;
import org.concordion.api.Command;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.util.Announcer;

public class ThrowableCatchingDecorator extends AbstractCommandDecorator {

    private final Announcer<ThrowableCaughtListener> listeners = Announcer.to(ThrowableCaughtListener.class);
    private final List<Class<? extends Throwable>> failFastExceptions;
    
    public void addThrowableListener(ThrowableCaughtListener listener) {
        listeners.addListener(listener);
    }

    public void removeThrowableListener(ThrowableCaughtListener listener) {
        listeners.removeListener(listener);
    }
    
    public ThrowableCatchingDecorator(Command command, List<Class<? extends Throwable>> failFastExceptions) {
        super(command);
        this.failFastExceptions = failFastExceptions;
    }

    private void announceThrowableCaught(Element element, Throwable t, String expression) {
        listeners.announce().throwableCaught(new ThrowableCaughtEvent(t, element, expression));
    }

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            resultRecorder.record(Result.EXCEPTION);
            announceThrowableCaught(commandCall.getElement(), t, commandCall.getExpression());
            for (Class<? extends Throwable> exceptionType : failFastExceptions) {
                if (exceptionType.isAssignableFrom(t.getCause().getClass())) {
                    throw new FailFastException("An exception was thrown", t);
                }
            }
        }        
    }
}

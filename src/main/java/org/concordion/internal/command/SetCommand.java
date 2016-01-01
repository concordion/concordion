package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.SetEvent;
import org.concordion.api.listener.SetListener;
import org.concordion.internal.util.Check;

public class SetCommand extends AbstractCommand {

    private List<SetListener> listeners = new ArrayList<SetListener>();

    public void addSetListener(SetListener listener) {
        listeners.add(listener);
    }

    public void removeSetListener(SetListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside a 'set' is not supported");
        evaluator.setVariable(commandCall.getExpression(), commandCall.getElement().getText());
        announceSetCompleted(commandCall.getElement(), commandCall.getExpression());
    }

    private void announceSetCompleted(Element element, String expression) {
        for (SetListener listener : listeners) {
			listener.setCompleted(new SetEvent(element, expression));
		}
    }
}

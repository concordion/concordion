package org.concordion.internal.command;

import org.concordion.api.AbstractCommand;
import org.concordion.api.Element;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.RowsListener;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.internal.util.Announcer;

public class AbstractRowsCommand extends AbstractCommand {

    private final Announcer<RowsListener> listeners = Announcer.to(RowsListener.class);

    public void addVerifyRowsListener(RowsListener listener) {
        listeners.addListener(listener);
    }

    public void removeVerifyRowsListener(RowsListener listener) {
        listeners.removeListener(listener);
    }

    protected final void announceExpressionEvaluated(Element element) {
        listeners.announce().expressionEvaluated(new ExpressionEvaluatedEvent(element));
    }

    protected final void announceMissingRow(Element element) {
        listeners.announce().missingRow(new MissingRowEvent(element));
    }

    protected final void announceSurplusRow(Element element) {
        listeners.announce().surplusRow(new SurplusRowEvent(element));
    }
}

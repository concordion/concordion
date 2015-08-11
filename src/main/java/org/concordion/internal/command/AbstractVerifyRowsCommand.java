package org.concordion.internal.command;

import org.concordion.api.AbstractCommand;
import org.concordion.api.Element;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.util.Announcer;

import java.util.regex.Pattern;

public class AbstractVerifyRowsCommand extends AbstractCommand {

    protected static final Pattern COMMAND_PATTERN = Pattern.compile("(#.+?) *: *(.+)");

    private Announcer<VerifyRowsListener> listeners = Announcer.to(VerifyRowsListener.class);

    public void addVerifyRowsListener(VerifyRowsListener listener) {
        listeners.addListener(listener);
    }

    public void removeVerifyRowsListener(VerifyRowsListener listener) {
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

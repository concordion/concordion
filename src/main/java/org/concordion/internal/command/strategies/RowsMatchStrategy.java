package org.concordion.internal.command.strategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for strategies for matching rows with the verify-rows command.
 * 
 * @since 2.0.0
 */
public abstract class RowsMatchStrategy {

    protected final CommandCall commandCall;
    protected final Evaluator evaluator;
    protected final ResultRecorder resultRecorder;
    protected final List<VerifyRowsListener> listeners;
    protected final String loopVariableName;
    protected final TableSupport tableSupport;
    protected final Row[] expectedRows;
    protected final List<Object> actualRows;

    public RowsMatchStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                             List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
        this.commandCall = commandCall;
        this.evaluator = evaluator;
        this.resultRecorder = resultRecorder;
        this.listeners = listeners;
        this.loopVariableName = loopVariableName;
        this.tableSupport = new TableSupport(commandCall);
        this.expectedRows = tableSupport.getDetailRows();
        this.actualRows = copy(actualRows);
    }

    public abstract void verify();

    protected void announceExpressionEvaluated(Element element) {
    	for (VerifyRowsListener listener : listeners) {
    		listener.expressionEvaluated(new ExpressionEvaluatedEvent(element));
		}
    }

    protected void announceMissingRow(Element element) {
    	for (VerifyRowsListener listener : listeners) {
    		listener.missingRow(new MissingRowEvent(element));
		}
    }

    protected void announceSurplusRow(Element element) {
    	for (VerifyRowsListener listener : listeners) {
    		listener.surplusRow(new SurplusRowEvent(element));
		}
    }

    protected List<Object> copy(Iterable<Object> iterable) {
        List<Object> copy = new ArrayList<Object>();
        for (Object o : iterable) {
            copy.add(o);
        }
        return copy;
    }
}
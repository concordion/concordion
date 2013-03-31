package org.concordion.internal.command;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.util.Announcer;

public class ExecuteCommand extends AbstractCommand {

    private Announcer<ExecuteListener> listeners = Announcer.to(ExecuteListener.class);

    public void addExecuteListener(ExecuteListener listener) {
        listeners.addListener(listener);
    }

    public void removeExecuteListener(ExecuteListener listener) {
        listeners.removeListener(listener);
    }
    
    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Strategy strategy;
        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else {
            strategy = new DefaultStrategy();
        }
        strategy.execute(commandCall, evaluator, resultRecorder);
    }
    
    private interface Strategy {
        void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);
    }
    
    private class DefaultStrategy implements Strategy {

        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            CommandCallList childCommands = commandCall.getChildren();
            
            childCommands.setUp(evaluator, resultRecorder);
            evaluator.evaluate(commandCall.getExpression());
            childCommands.execute(evaluator, resultRecorder);
            announceExecuteCompleted(commandCall.getElement());
            childCommands.verify(evaluator, resultRecorder);
        }
    }
    
    private class TableStrategy implements Strategy {

        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            TableSupport tableSupport = new TableSupport(commandCall);
            Row[] detailRows = tableSupport.getDetailRows();
            for (Row detailRow : detailRows) {
                if (detailRow.getCells().length != tableSupport.getColumnCount()) {
                    throw new RuntimeException("The <table> 'execute' command only supports rows with an equal number of columns.");
                }
                commandCall.setElement(detailRow.getElement());
                tableSupport.copyCommandCallsTo(detailRow);
                commandCall.execute(evaluator, resultRecorder);
            }
        }
    }
    
    private void announceExecuteCompleted(Element element) {
        listeners.announce().executeCompleted(new ExecuteEvent(element));
    }

}

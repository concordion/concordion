package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.internal.ListEntry;
import org.concordion.internal.ListSupport;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;

public class ExecuteCommand extends AbstractCommand {

    private List<ExecuteListener> listeners = new ArrayList<ExecuteListener>();

    public void addExecuteListener(ExecuteListener listener) {
        listeners.add(listener);
    }

    public void removeExecuteListener(ExecuteListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Strategy strategy;
        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else if (commandCall.getElement().isNamed("ol") || commandCall.getElement().isNamed("ul")) {
        	strategy = new ListStrategy();
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
    
    private class ListStrategy implements Strategy {
    	
    	private static final String LEVEL_VARIABLE = "#LEVEL";

		@Override
		public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
			increaseLevel(evaluator);
			ListSupport listSupport = new ListSupport(commandCall);
			for (ListEntry listEntry : listSupport.getListEntries()) {
                commandCall.setElement(listEntry.getElement());
                if (listEntry.isItem())
                {
                    commandCall.execute(evaluator, resultRecorder);
                }
                if (listEntry.isList())
                {
                    execute(commandCall, evaluator, resultRecorder);
                }
            }
            decreaseLevel(evaluator);
		}

		private void increaseLevel(Evaluator evaluator) {
			Integer value = (Integer) evaluator.getVariable(LEVEL_VARIABLE);
			if (value == null) {
				value = 0;
			}
			evaluator.setVariable(LEVEL_VARIABLE, value + 1);
		}

		private void decreaseLevel(Evaluator evaluator) {
			Integer value = (Integer) evaluator.getVariable(LEVEL_VARIABLE);
			evaluator.setVariable(LEVEL_VARIABLE, value - 1);
		}
    	
    }
    
    private void announceExecuteCompleted(Element element) {
        for (ExecuteListener listener : listeners) {
			listener.executeCompleted(new ExecuteEvent(element));
		}
    }

}

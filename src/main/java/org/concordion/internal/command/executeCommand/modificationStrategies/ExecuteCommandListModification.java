package org.concordion.internal.command.executeCommand.modificationStrategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ExampleCommandCall;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.ListEntry;
import org.concordion.internal.ListSupport;

import java.util.List;

/**
 * Created by tim on 30/10/16.
 */
public class ExecuteCommandListModification extends ExecuteCommandModification {
    private static final String LEVEL_VARIABLE = "#LEVEL";

    @Override
    public void performModification(CommandCall commandCall, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
        /*
            We set the bypass flag on the main <table> node so it's not executed anymore. We
            can't really remove it because if we remove it, we'll have to add all the new children to
            this node's parent node. However, the 'modify' method is currently processing the parent node
            and has made a copy of the list of children (to avoid a concurrent modification exception).

            This means that any children added to the parent node won't have 'modify' called on them. And if
            any of the children are examples, then they won't be processed properly.
         */
        commandCall.setBypassExecution(true);

        ListSupport listSupport = new ListSupport(commandCall);
        for (ListEntry listEntry : listSupport.getListEntries()) {
            commandCall.setElement(listEntry.getElement());
            if (listEntry.isItem()) {
                CommandCall itemCommand = duplicateCommandForDifferentElement(commandCall, listEntry.getElement());
                itemCommand.setConstantForExecution(LEVEL_VARIABLE, getLevel(commandCall));
                itemCommand.transferToParent(commandCall);
            }
            if (listEntry.isList()) {
                // recursive call
                CommandCall itemCommand = duplicateCommandForDifferentElement(commandCall, listEntry.getElement());
                itemCommand.setConstantForExecution(LEVEL_VARIABLE, getLevel(commandCall) + 1);
                itemCommand.transferToParent(commandCall);
            }
        }
    }

    private int getLevel(CommandCall commandCall) {
        Integer currentLevel = (Integer)commandCall.getConstantForExecution(LEVEL_VARIABLE);
        return currentLevel == null ? 1 : currentLevel;
    }
}

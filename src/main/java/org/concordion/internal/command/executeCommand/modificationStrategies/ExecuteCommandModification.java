package org.concordion.internal.command.executeCommand.modificationStrategies;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.ExampleCommandCall;

import java.util.List;

/**
 * Created by tim on 30/10/16.
 */
public abstract class ExecuteCommandModification {
    public abstract void performModification(CommandCall commandCall, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples);

    protected CommandCall duplicateCommandForDifferentElement(CommandCall commandCall, Element element) {
        return new CommandCall(
                null,
                commandCall.getCommand(),
                element,
                commandCall.getExpression(),
                commandCall.getResource());
    }

}

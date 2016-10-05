package org.concordion.internal.command.executeCommand;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.command.ExampleCommand;

import java.util.List;

/**
 * Created by tim on 12/06/16.
 */
interface ExecuteCommandStrategy {
    void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    List<CommandCall> getExamples(CommandCall command);
}

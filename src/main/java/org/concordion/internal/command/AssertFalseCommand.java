package org.concordion.internal.command;

import org.concordion.api.CommandCall;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;

public class AssertFalseCommand extends BooleanCommand {

    @Override
    protected void processFalseResult(CommandCall commandCall, ResultRecorder resultRecorder) {
        resultRecorder.record(Result.SUCCESS);
        announceSuccess(commandCall.getElement());
    }

    @Override
    protected void processTrueResult(CommandCall commandCall, ResultRecorder resultRecorder) {
        resultRecorder.record(Result.FAILURE);
        String expected = commandCall.getElement().getText();
        announceFailure(commandCall.getElement(), expected, "== true");
    }

}

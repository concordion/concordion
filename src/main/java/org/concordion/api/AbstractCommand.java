package org.concordion.api;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand implements Command {



    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public Result verifyInBackground(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        return Result.IGNORED;
    }

    public boolean isExample() {
        return false;
    }

    public List<CommandCall> getExamples(CommandCall command) {
        return Collections.emptyList();
    }

    public void executeAsExample(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void finish(CommandCall commandCall) {
    }
}

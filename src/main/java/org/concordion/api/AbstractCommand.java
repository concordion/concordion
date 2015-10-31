package org.concordion.api;


import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand implements Command {

    public List<CommandCall> getExamples(CommandCall command) {
        return Collections.emptyList();
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    public boolean isExample() {
        return false;
    }

    public void executeAsExample(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }
}

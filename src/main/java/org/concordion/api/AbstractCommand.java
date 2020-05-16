package org.concordion.api;


import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand implements Command {

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
    }

    @Override
    public void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
    }
}

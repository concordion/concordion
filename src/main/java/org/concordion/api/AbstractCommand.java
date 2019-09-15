package org.concordion.api;


import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand implements Command {

    @Deprecated
    public List<CommandCall> getExamples(CommandCall command) {
        return Collections.emptyList();
    }

    @Deprecated
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    @Deprecated
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    @Deprecated
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    }

    // For backwards compatibility, call the deprecated methods
    // TODO - remove this in Concordion 3.0.0
    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        execute(commandCall, evaluator, resultRecorder);
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        setUp(commandCall, evaluator, resultRecorder);
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        verify(commandCall, evaluator, resultRecorder);
    }

    @Override
    public void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
    }
}

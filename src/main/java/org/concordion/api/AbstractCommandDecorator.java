package org.concordion.api;


import java.util.List;

public abstract class AbstractCommandDecorator implements Command {

    private final Command command;

    public AbstractCommandDecorator(Command command) {
        this.command = command;
    }

    public void setUp(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder, final Fixture fixture) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.setUp(commandCall, evaluator, resultRecorder, fixture);
            }
        });
    }

    public void execute(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder, final Fixture fixture) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.execute(commandCall, evaluator, resultRecorder, fixture);
            }
        });
    }
    
    public void verify(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder, final Fixture fixture) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.verify(commandCall, evaluator, resultRecorder, fixture);
            }
        });
    }

    @Override
    public void modifyCommandCallTree(final CommandCall element, final List<ExampleCommandCall> examples, final List<CommandCall> beforeExamples) {
        command.modifyCommandCallTree(element, examples, beforeExamples);
    }

    protected abstract void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable);
}

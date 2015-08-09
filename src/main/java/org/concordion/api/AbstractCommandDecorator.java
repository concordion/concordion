package org.concordion.api;


public abstract class AbstractCommandDecorator implements Command {

    private final Command command;

    public AbstractCommandDecorator(Command command) {
        this.command = command;
    }

    public void setUp(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.setUp(commandCall, evaluator, resultRecorder);
            }
        });
    }

    public void execute(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.execute(commandCall, evaluator, resultRecorder);
            }
        });
    }
    
    public void verify(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder) {
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                command.verify(commandCall, evaluator, resultRecorder);
            }
        });
    }

    public Result verifyInBackground(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder) {
        final Result[] resultHolder = {Result.EXCEPTION};
        process(commandCall, evaluator, resultRecorder, new Runnable() {
            public void run() {
                resultHolder[0] = command.verifyInBackground(commandCall, evaluator, resultRecorder);
            }
        });
        return resultHolder[0];
    }

    protected abstract void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable);
}

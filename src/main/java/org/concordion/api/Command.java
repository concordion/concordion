package org.concordion.api;


import java.util.List;

public interface Command {

    void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    List<CommandCall> getExamples(CommandCall command);

    boolean isExample();

    void executeAsExample(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    /**
     *
     * Basically we have a rule to only run examples if there are execute statements
     * in the example. However, with the table execute command, the execute statement
     * is part of the table header - so the "one example per row" examples were not
     * executing. This method was my work-around on top of a work-around.
     *
     * I do actually prefer a marker interface in this case. However, I don't actually
     * like marker interfaces because there is no way for a sub-class to remove the marker.
     *
     * In general, this method should return false.
     *
     * @return true if Concordion should not check for any non-example children and just run the command anyway
     */
    boolean shouldExecuteEvenWhenAllChildCommandsAreExamples();
}

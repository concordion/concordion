package org.concordion.api;

import java.util.List;

public interface Command {

    void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    Result verifyInBackground(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void finish(CommandCall commandCall);

    List<CommandCall> getExamples(CommandCall command);

    boolean isExample();

    void executeAsExample(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

}

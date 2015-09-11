package org.concordion.api;

import java.util.ArrayList;
import java.util.List;


public class CommandCallList {

    private List<CommandCall> commandCalls = new ArrayList<CommandCall>();
    
    public boolean isEmpty() {
        return commandCalls.isEmpty();
    }

    public void setUp(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : commandCalls) call.setUp(evaluator, resultRecorder);
    }
    
    public void execute(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : commandCalls) call.execute(evaluator, resultRecorder);
    }

    public void verify(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : commandCalls) call.verify(evaluator, resultRecorder);
    }

    public Result verifyInBackground(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : commandCalls) {
            if (call.verifyInBackground(evaluator, resultRecorder) != Result.SUCCESS) {
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }

    public void processSequentially(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : commandCalls) {
            call.setUp(evaluator, resultRecorder);
            call.execute(evaluator, resultRecorder);
            call.verify(evaluator, resultRecorder);
        }
    }
    
    public void append(CommandCall commandCall) {
        commandCalls.add(commandCall);
    }

    public int size() {
        return commandCalls.size();
    }
    
    public CommandCall get(int index) {
        return commandCalls.get(index);
    }
}

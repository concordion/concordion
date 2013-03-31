package org.concordion.api;

public class RunnerResult {

    private final Result result;

    public RunnerResult(Result result) {
        this.result = result;
    }
    
    public Result getResult() {
        return result;
    }
}

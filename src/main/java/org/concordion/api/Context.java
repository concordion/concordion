package org.concordion.api;

public interface Context extends ResultRecorder, Evaluator {

    Resource getResource();
    
    Element getElement();
    
    String getExpression();

    Object evaluateExpression();

    void processChildCommandsSequentially();

    boolean hasChildCommands();

    void setUpChildCommands();

    void executeChildCommands();
    
    void verifyChildCommands();
}

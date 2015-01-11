package org.concordion.internal;

import org.concordion.api.*;

public class XMLSpecification extends AbstractSpecification {

    private final CommandCall rootCommandNode;

    public XMLSpecification(CommandCall rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
    }
    
    public void process(Evaluator evaluator, ResultRecorder resultRecorder) {
        rootCommandNode.execute(evaluator, resultRecorder);
    }
}

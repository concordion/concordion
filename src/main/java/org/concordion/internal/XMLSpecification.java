package org.concordion.internal;

import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Specification;

public class XMLSpecification implements Specification {

    private final CommandCall rootCommandNode;

    public XMLSpecification(CommandCall rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
    }
    
    public void process(Evaluator evaluator, ResultRecorder resultRecorder) {
        rootCommandNode.execute(evaluator, resultRecorder);
    }
}

package org.concordion.internal.command;

import org.concordion.api.*;
import org.concordion.internal.util.Check;

public class EchoCommand extends AbstractCommand {

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'echo' is not supported");
        
        Object result = evaluator.evaluate(commandCall.getExpression());

        Element element = commandCall.getElement();
        if (result != null) {
            element.appendText(result.toString());
        } else {
            Element child = new Element("em");
            child.appendText("null");
            element.appendChild(child);
        }
    }
}

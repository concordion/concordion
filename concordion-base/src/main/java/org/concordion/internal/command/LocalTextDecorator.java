package org.concordion.internal.command;

import org.concordion.api.AbstractCommandDecorator;
import org.concordion.api.Command;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class LocalTextDecorator extends AbstractCommandDecorator {

    private static final String TEXT_VARIABLE = "#TEXT";
    private static final String HREF_VARIABLE = "#HREF";

    public LocalTextDecorator(Command command) {
        super(command);
    }

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        Object savedTextValue = evaluator.getVariable(TEXT_VARIABLE);
        Object savedHrefValue = evaluator.getVariable(HREF_VARIABLE);
        try {
            evaluator.setVariable(TEXT_VARIABLE, commandCall.getElement().getText());
            evaluator.setVariable(HREF_VARIABLE, getHref(commandCall.getElement()));
            runnable.run();
        } finally {
            evaluator.setVariable(TEXT_VARIABLE, savedTextValue);
            evaluator.setVariable(HREF_VARIABLE, savedHrefValue);
        }
    }

    private String getHref(Element element) {
        String href = element.getAttributeValue("href");
        if (href == null) {
            Element a = element.getFirstChildElement("a");
            if (a != null) {
                href = a.getAttributeValue("href");
            }
        }
        return href;
    }
}

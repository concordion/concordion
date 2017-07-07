package test.concordion.extension;

import org.concordion.api.Element;
import org.concordion.api.listener.SetEvent;
import org.concordion.api.listener.SetListener;

import java.io.PrintStream;

public class SetLogger implements SetListener {

    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    public void setCompleted(SetEvent event) {
        Element element = event.getElement();
        stream.println("Set " + getTargetExpression(event) + " = '" + element.getText() + "'");
    }

    private String getTargetExpression(SetEvent event) {
        if (event.getElement().getAttributeValue("set") != null) {
            return event.getElement().getAttributeValue("set");
        } else {
            return event.getExpression();
        }
    }
}

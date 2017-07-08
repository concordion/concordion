package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.Element;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;

public class ExecuteLogger implements ExecuteListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }
    
    public void executeCompleted(ExecuteEvent e) {
        Element element = e.getElement();
        if (element.getLocalName().equals("tr")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Execute '");
            Element[] childElements = element.getChildElements();
            boolean firstChild = true;
            for (Element child : childElements) {
                if (firstChild) {
                    firstChild = false;
                } else {
                    sb.append(", ");
                }
                sb.append(child.getText());
            }
            sb.append("'");
            stream.println(sb.toString());
        } else {
            stream.println("Execute '" + element.getText() + "'");
        }
    }
}
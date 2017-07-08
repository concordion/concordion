package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.api.listener.AssertTrueListener;

public class AssertLogger implements AssertEqualsListener, AssertTrueListener, AssertFalseListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }
    
    public void successReported(AssertSuccessEvent event) {
        stream.println("Success '" + event.getElement().getText() + "'");
    }

    public void failureReported(AssertFailureEvent event) {
        stream.println("Failure expected:'" + event.getExpected() + "' actual:'" + event.getActual() + "'");
    }
}
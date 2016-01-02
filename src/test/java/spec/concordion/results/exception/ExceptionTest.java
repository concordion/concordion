package spec.concordion.results.exception;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.listener.ThrowableRenderer;
import org.concordion.internal.util.IOUtil;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ExceptionTest {
    
    private List<StackTraceElement> stackTraceElements = new ArrayList<StackTraceElement>();
    
    public void addStackTraceElement(String declaringClassName, String methodName, String filename, int lineNumber) {
        if (filename.equals("null")) {
            filename = null;
        }
        stackTraceElements.add(new StackTraceElement(declaringClassName, methodName, filename, lineNumber));
    }
    
    public String markAsException(String fragment, String expression, String errorMessage) {
        Throwable t = new Throwable(errorMessage);
        t.setStackTrace(stackTraceElements.toArray(new StackTraceElement[0]));
        
        Element element = new Element((nu.xom.Element) new TestRig()
            .processFragment(fragment)
            .getXOMDocument()
            .query("//p")
            .get(0));
            
        new ThrowableRenderer(new IOUtil()).throwableCaught(new ThrowableCaughtEvent(t, element, expression));
        
        return element.toXML();
    }
}

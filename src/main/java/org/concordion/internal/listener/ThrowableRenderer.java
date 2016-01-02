package org.concordion.internal.listener;

import java.util.HashSet;
import java.util.Set;

import org.concordion.api.Element;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.internal.FailFastException;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;

public class ThrowableRenderer implements ThrowableCaughtListener {

    private static final String TOGGLING_SCRIPT_RESOURCE_PATH = "/org/concordion/internal/resource/visibility-toggler.js";
    private long buttonId = 0;
    private Set<Element> rootElementsWithScript = new HashSet<Element>();
	private IOUtil ioUtil;
    
    public ThrowableRenderer(IOUtil ioUtil) {
    	this.ioUtil = ioUtil;
    }
    
    public void throwableCaught(ThrowableCaughtEvent event) {
        Element element = event.getElement();
        if (!(event.getThrowable() instanceof FailFastException)) {
            buttonId++;
            
            element.appendChild(expectedSpan(element));
            
            // Special handling for <a> tags to avoid the stack-trace being inside the link text
            if (element.getLocalName().equals("a")) {
                Element div = new Element("div"); 
                element.appendSister(div);
                element = div;
            }
            element.appendChild(exceptionMessage(event.getThrowable().getMessage()));
            element.appendChild(stackTraceTogglingButton());
            element.appendChild(stackTrace(event.getThrowable(), event.getExpression()));
            
            ensureDocumentHasTogglingScript(element);
        } else {
            element.addStyleClass("failure");
        }
    }

    private void ensureDocumentHasTogglingScript(Element element) {
        Element rootElement = element.getRootElement();
        if (!rootElementsWithScript.contains(rootElement)) {
            rootElementsWithScript.add(rootElement);
            Element head = rootElement.getFirstDescendantNamed("head");
            if (head == null) {
                System.out.println(rootElement.toXML());
            }
            Check.notNull(head, "Document <head> section is missing");
            Element script = new Element("script").addAttribute("type", "text/javascript");
            head.prependChild(script);
            script.appendText(ioUtil.readResourceAsString(TOGGLING_SCRIPT_RESOURCE_PATH));
            
        }
    }

    private Element expectedSpan(Element element) {
        Element spanExpected = new Element("del").addStyleClass("expected");
        element.moveChildrenTo(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();
        Element spanFailure = new Element("span").addStyleClass("failure");
        spanFailure.appendChild(spanExpected);
        return spanFailure;
    }

    private Element exceptionMessage(String exceptionMessage) {
        return new Element("span")
                .addStyleClass("exceptionMessage")
                .appendText(exceptionMessage);
    }

    private Element stackTraceTogglingButton() {
        return new Element("input")
                .addStyleClass("stackTraceButton")
                .setId("stackTraceButton" + buttonId)
                .addAttribute("type", "button")
                .addAttribute("onclick", "javascript:toggleStackTrace('" + buttonId + "')")
                .addAttribute("value", "View Stack");
    }
    
    private Element stackTrace(Throwable t, String expression) {
        Element stackTrace = new Element("div").addStyleClass("stackTrace");
        stackTrace.setId("stackTrace" + buttonId);
        
        Element p = new Element("p")
                .appendText("While evaluating expression: ");
        p.appendChild(new Element("code").appendText(expression));
        stackTrace.appendChild(p);
        
        recursivelyAppendStackTrace(t, stackTrace);

        return stackTrace;
    }

    private void recursivelyAppendStackTrace(Throwable t, Element stackTrace) {
        Element stackTraceExceptionMessage = new Element("div")
                .addStyleClass("stackTraceExceptionMessage")
                .appendText(t.getClass().getName() + ": " + t.getMessage());
        stackTrace.appendChild(stackTraceExceptionMessage);

        for (StackTraceElement traceElement : t.getStackTrace()) {
            stackTrace.appendChild(stackTraceElement(traceElement));
        }

        if (t.getCause() != null) {
            recursivelyAppendStackTrace(t.getCause(), stackTrace);
        }
    }

    private Element stackTraceElement(StackTraceElement traceElement) {
        Element entry = new Element("div")
                .addStyleClass("stackTraceEntry")
                .appendText("at " + traceElement.getClassName())
                .appendText("." + traceElement.getMethodName());
        if (traceElement.getFileName() == null) {
            entry.appendText(" (Unknown Source)");
        } else {
            entry.appendText(" (" + traceElement.getFileName() + ":" + traceElement.getLineNumber() + ")");
        }
        return entry;
    }

}

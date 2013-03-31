package test.concordion;

import nu.xom.Document;

import org.concordion.api.Element;
import org.concordion.api.ResultSummary;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.internal.XMLParser;

public class ProcessingResult {

    private final ResultSummary resultSummary;
    private final EventRecorder eventRecorder;
    private final String documentXML;

    public ProcessingResult(ResultSummary resultSummary, EventRecorder eventRecorder, String documentXML) {
        this.resultSummary = resultSummary;
        this.eventRecorder = eventRecorder;
        this.documentXML = documentXML;
    }
    
    public long getSuccessCount() {
        return resultSummary.getSuccessCount();
    }

    public long getFailureCount() {
        return resultSummary.getFailureCount();
    }

    public long getExceptionCount() {
        return resultSummary.getExceptionCount();
    }
    
    public AssertFailureEvent getLastAssertFailureEvent() {
        return (AssertFailureEvent) eventRecorder.getLast(AssertFailureEvent.class);
    }

    public Document getXOMDocument() {
        try {
            return XMLParser.parse(documentXML);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse resultant XML document", e);
        }
    }
    
    public Element getRootElement() {
        return new Element(getXOMDocument().getRootElement());
    }
    
    public String toXML() {
        return getRootElement().toXML();
    }

    public String successOrFailureInWords() {
        return hasFailures()  ? "FAILURE" : "SUCCESS";
    }

    public boolean hasFailures() {
        return getFailureCount() + getExceptionCount() != 0;
    }

    public boolean isSuccess() {
        return !hasFailures();
    }

    private Element getOutputFragment() {
        return getRootElement().getFirstDescendantNamed("fragment");
    }

    public String getOutputFragmentXML() {
        return getOutputFragment().toXML().replaceAll("</?fragment>", "").replaceAll("\u00A0", "&#160;");
    }

    public boolean hasCSSDeclaration(String cssFilename) {
        Element head = getRootElement().getFirstChildElement("head");
        for (Element link : head.getChildElements("link")) {
            String href = link.getAttributeValue("href");
            String type = link.getAttributeValue("type");
            String rel = link.getAttributeValue("rel");
            if (cssFilename.equals(href) 
                    && "text/css".equals(type)
                    && "stylesheet".equals(rel)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmbeddedCSS(String css) {
        Element head = getRootElement().getFirstChildElement("head");
        for (Element style : head.getChildElements("style")) {
            if (style.getText().contains(css) ) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJavaScriptDeclaration(String cssFilename) {
        Element head = getRootElement().getFirstChildElement("head");
        for (Element script : head.getChildElements("script")) {
            String type = script.getAttributeValue("type");
            String src = script.getAttributeValue("src");
            if ("text/javascript".equals(type) && cssFilename.equals(src)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmbeddedJavaScript(String javaScript) {
        Element head = getRootElement().getFirstChildElement("head");
        for (Element script : head.getChildElements("script")) {
            String type = script.getAttributeValue("type");
            if ("text/javascript".equals(type) && script.getText().contains(javaScript)) {
                return true;
            }
        }
        return false;
    }
}

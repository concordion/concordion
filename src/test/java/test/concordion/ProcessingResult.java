package test.concordion;

import nu.xom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

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
    
    public String getFooterText() {
        Element[] childDivs = getRootElement().getDescendantElements("div");
        return textOfElementWithClass(childDivs, "footer");
    }

    public String getElementXML(String className) {
        Element[] childDivs = getRootElement().getDescendantElements("div");

        for (Element div : childDivs) {
            if (className.equals(div.getAttributeValue("class"))) {
                return div.toXML();
            }
        }
        return "";
    }

    public String getExceptionMessage() {
        Element[] childSpans = getRootElement().getDescendantElements("span");
        return textOfElementWithClass(childSpans, "exceptionMessage");
    }
    
    
    public String getStackTraceMessage() {
        String clazz = "stackTraceExceptionMessage";
        Element[] childDivs = getRootElement().getDescendantElements("div");   // was changed from span to div in Concordion 1.4.2
        String message = textOfElementWithClass(childDivs, clazz);
        if (message == null) {
            Element[] childSpans = getRootElement().getDescendantElements("span");
            message = textOfElementWithClass(childSpans, clazz);
        }
        return message;
    }
    
    private String textOfElementWithClass(Element[] elements, String clazz) {
        for (Element div : elements) {
            if (clazz.equals(div.getAttributeValue("class"))) {
                return div.getText();
            }
        }
        return "";
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

    private Element getHeadElement() {
        return getRootElement().getFirstChildElement("head");
    }
    
    public boolean hasCSSDeclaration(String cssFilename) {
        Element head = getHeadElement();
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
        Element head = getHeadElement();
        for (Element style : head.getChildElements("style")) {
            if (style.getText().contains(css) ) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLinkedCSS(String baseOutputDir, String css) {
        for (Element style : getHeadElement().getChildElements("link")) {
            if ("stylesheet".equals(style.getAttributeValue("rel")) && css.endsWith(style.getAttributeValue("href"))) {
            	String path = Paths.get(baseOutputDir, style.getAttributeValue("href")).toString();
                return new File(path).exists();
            }
        }
        return false;
    }
    
    public String getLinkedCSS(String baseOutputDir, String css) throws IOException {
    	for (Element style : getHeadElement().getChildElements("link")) {
    		if ("stylesheet".equals(style.getAttributeValue("rel")) && css.endsWith(style.getAttributeValue("href"))) {
    			String path = Paths.get(baseOutputDir, style.getAttributeValue("href")).toString();
    			return readFile(path);
    		}
    	}
    	return "";
    }
    
    public boolean hasJavaScriptDeclaration(String cssFilename) {
        Element head = getHeadElement();
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
        Element head = getHeadElement();
        for (Element script : head.getChildElements("script")) {
            String type = script.getAttributeValue("type");
            if ("text/javascript".equals(type) && script.getText().contains(javaScript)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasLinkedJavaScript(String baseOutputDir, String javaScript) {
        for (Element style : getHeadElement().getChildElements("script")) {
            if ("text/javascript".equals(style.getAttributeValue("type")) && javaScript.equals(style.getAttributeValue("src"))) {
            	String path = Paths.get(baseOutputDir, style.getAttributeValue("src")).toString();
                return new File(path).exists();
            }
        }
        return false;
    }

    public String getLinkedJavaScript(String baseOutputDir, String javaScript) throws IOException {
    	for (Element style : getHeadElement().getChildElements("script")) {
            if ("text/javascript".equals(style.getAttributeValue("type")) && javaScript.equals(style.getAttributeValue("src"))) {
            	String path = Paths.get(baseOutputDir, style.getAttributeValue("src")).toString();
                return readFile(path);
            }
    	}
    	return "";
    }
    
    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }   
}
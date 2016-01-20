package org.concordion.internal.listener;

import java.io.IOException;

import nu.xom.Document;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.XMLParser;
import org.concordion.internal.util.Check;

public class BreadcrumbRenderer implements SpecificationProcessingListener {

    private final Source source;
    private final XMLParser xmlParser;

    public BreadcrumbRenderer(Source source, XMLParser xmlParser) {
        this.source = source;
        this.xmlParser = xmlParser;
    }
    
    public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
        // No action needed beforehand
    }
    
    public void afterProcessingSpecification(SpecificationProcessingEvent event) {
        try {
            Element span = new Element("span").addStyleClass("breadcrumbs");
            appendBreadcrumbsTo(span, event.getResource());
    
            if (span.hasChildren()) {
                getDocumentBody(event.getRootElement())
                    .prependChild(span);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Element getDocumentBody(Element rootElement) {
        Element body = rootElement.getFirstDescendantNamed("body");
        if (body == null) {
            body = new Element("body");
            rootElement.appendChild(body);
        }
        return body;
    }

    private void appendBreadcrumbsTo(Element breadcrumbSpan, Resource documentResource) {
        
        Resource packageResource = documentResource.getParent();
        
        while (packageResource != null) {
            Resource indexPageResource = packageResource.getRelativeResource(getIndexPageName(packageResource));
            if (!indexPageResource.equals(documentResource) && source.canFind(indexPageResource)) {
                try {
                    prependBreadcrumb(breadcrumbSpan, createBreadcrumbElement(documentResource, indexPageResource));
                } catch (Exception e) {
                	Check.isTrue(false, "Failed to generate breadcrumb");
                }
            }
            packageResource = packageResource.getParent();
        }

    }

    private String getIndexPageName(Resource packageResource) {
        return capitalize(packageResource.getName()) + ".html";
    }

    private void prependBreadcrumb(Element span, Element breadcrumb) {
        if (span.hasChildren()) {
            span.prependText(" ");
        }
        span.prependText(" >");
        span.prependChild(breadcrumb);
    }

    private Element createBreadcrumbElement(Resource documentResource, Resource indexPageResource) throws IOException  {

        Document document = xmlParser.parse(source.createInputStream(indexPageResource), "[" + source + ": " + indexPageResource.getPath() + "]");

        String breadcrumbWording = getBreadcrumbWording(new Element(document.getRootElement()), indexPageResource);
        Element a = new Element("a");
        a.addAttribute("href", documentResource.getRelativePath(indexPageResource));
        a.appendText(breadcrumbWording);
        return a;
    }

    private String getBreadcrumbWording(Element rootElement, Resource resource) {
        Element title = rootElement.getFirstDescendantNamed("title");
        if ((title != null) && !isBlank(title.getText())) {
            return title.getText();
        }
        Element[] headings = rootElement.getDescendantElements("h1");
        for (Element h1 : headings) {
            if ((h1 != null) && !isBlank(h1.getText())) {
                return h1.getText();
            }
        }
        if (resource != null) {
            String heading = resource.getName();
            if (!isBlank(heading)) {
                heading = stripExtension(heading);
                heading = capitalize(heading);
                heading = deCamelCase(heading);
                return heading;
            }
        }
        return "(Up)";
    }

    private static String capitalize(String s) {
        if (s.equals("")) {
            return "";
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String stripExtension(String s) {
        return s.replaceAll("\\.[a-z]+", "");
    }

    private static String deCamelCase(String s) {
        return s.replaceAll("([0-9a-z])([A-Z])", "$1 $2");
    }

    private static boolean isBlank(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "").equals("");
    }

}
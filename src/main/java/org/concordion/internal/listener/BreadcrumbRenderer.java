package org.concordion.internal.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.api.SpecificationConverter;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.internal.SpecificationType;
import org.concordion.internal.XMLParser;
import org.concordion.internal.util.SimpleFormatter;

public class BreadcrumbRenderer implements SpecificationProcessingListener {

    private static Logger logger = Logger.getLogger(BreadcrumbRenderer.class.getName());
    private static Map<Resource, String> breadcrumbWordingCache = new ConcurrentHashMap<Resource, String>();
    private final Source source;
    private final XMLParser xmlParser;
    private List<SpecificationType> specificationTypes;

    public BreadcrumbRenderer(Source source, XMLParser xmlParser, List<SpecificationType> specificationTypes) {
        this.source = source;
        this.xmlParser = xmlParser;
        this.specificationTypes = specificationTypes;
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
            for (SpecificationType specificationType : specificationTypes) {
                Resource indexPageResource = packageResource.getRelativeResource(getIndexPageName(packageResource, specificationType.getTypeSuffix()));
                if (!indexPageResource.equals(documentResource) && source.canFind(indexPageResource)) {
                    try {
                        prependBreadcrumb(breadcrumbSpan, createBreadcrumbElement(documentResource, indexPageResource, specificationType.getConverter()));
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to generate breadcrumb", e);
                    }
                    break;
                }
            }
            packageResource = packageResource.getParent();
        }

    }

    private String getIndexPageName(Resource packageResource, String suffix) {
        return capitalize(packageResource.getName()) + "." + suffix;
    }

    private void prependBreadcrumb(Element span, Element breadcrumb) {
        if (span.hasChildren()) {
            span.prependText(" ");
        }
        span.prependText(" >");
        span.prependChild(breadcrumb);
    }

    private Element createBreadcrumbElement(Resource documentResource, Resource indexPageResource, SpecificationConverter specificationConverter) throws IOException  {

        String breadcrumbWording = getBreadcrumbWordingForResource(indexPageResource, specificationConverter);
        Element a = new Element("a");
        Resource indexPageAsHtmlResource = new Resource(indexPageResource.getPath().replaceFirst("\\..*$", "\\.html"));
        a.addAttribute("href", documentResource.getRelativePath(indexPageAsHtmlResource));
        a.appendText(breadcrumbWording);
        return a;
    }

    private String getBreadcrumbWordingForResource(Resource indexPageResource, SpecificationConverter specificationConverter)
            throws IOException {
        String breadcrumbWording = breadcrumbWordingCache.get(indexPageResource);
        if (breadcrumbWording == null) {
            InputStream inputStream = source.createInputStream(indexPageResource);
            if (specificationConverter != null) {
                inputStream = specificationConverter.convert(inputStream);
            }
            Document document = xmlParser.parse(inputStream, SimpleFormatter.format("[%s: %s]", source, indexPageResource.getPath()));

            breadcrumbWording = getBreadcrumbWording(new Element(document.getRootElement()), indexPageResource);
            breadcrumbWordingCache.put(indexPageResource, breadcrumbWording);
        }
        return breadcrumbWording;
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
        return s.substring(0, 1).toUpperCase() + s.substring(1);
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
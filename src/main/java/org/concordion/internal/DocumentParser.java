package org.concordion.internal;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Elements;

import org.concordion.api.Command;
import org.concordion.api.CommandCall;
import org.concordion.api.CommandFactory;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Specification;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.Check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentParser {

    private final CommandFactory commandFactory;
    private final List<DocumentParsingListener> listeners = Collections.synchronizedList(new ArrayList<DocumentParsingListener>());
    
    public DocumentParser(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public void addDocumentParsingListener(DocumentParsingListener listener) {
        listeners.add(listener);
    }

    public void removeDocumentParsingListener(DocumentParsingListener listener) {
        listeners.remove(listener);
    }
    
    private void announceBeforeParsing(Document document) {
    	for (DocumentParsingListener listener : listeners) {
    		listener.beforeParsing(document);
		}
    }

    public Specification parse(Document document, Resource resource) {
        announceBeforeParsing(document);
        nu.xom.Element xomElement = document.getRootElement();
        CommandCall rootCommandCall = new CommandCall(createSpecificationCommand(), new Element(xomElement), "", resource);
        generateCommandCallTree(xomElement, rootCommandCall, resource);
        return new XMLSpecification(rootCommandCall);
    }

    private void generateCommandCallTree(nu.xom.Element xomElement, CommandCall parentCommandCall, Resource resource) {
        boolean commandIsAssigned = false;

        Command command = null;
        CommandCall commandCall = null;
        String namespaceURI = null;

        for (int i = 0; i < xomElement.getAttributeCount(); i++) {
            Attribute attribute = xomElement.getAttribute(i);
            namespaceURI = attribute.getNamespaceURI();

            if (!namespaceURI.equals("")) {
                String commandName = attribute.getLocalName();
                command = createCommand(namespaceURI, commandName);
                if (command != null) {
                    Check.isFalse(commandIsAssigned, "Multiple commands per element is currently not supported.");
                    String expression = attribute.getValue();
                    commandCall = new CommandCall(command, new Element(xomElement), expression, resource);
                    break;
                }
            }
        }

        if (commandCall != null) {
            Map<String, String> params = new HashMap<String, String>();

            // assume that because the commandCall is not null that the namespaceURI has also been set
            for (int i = 0; i < xomElement.getAttributeCount(); i++) {
                Attribute attribute = xomElement.getAttribute(i);
                if (namespaceURI.equals(attribute.getNamespaceURI())) {
                    // something in the same namespace. Assume it is a parameter of some
                    // description
                    params.put(attribute.getLocalName(), attribute.getValue());
                }
            }

            parentCommandCall.appendChild(commandCall);
            parentCommandCall = commandCall;
            commandCall.setParameters(params);
        }

        Elements children = xomElement.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            generateCommandCallTree(children.get(i), parentCommandCall, resource);
        }
    }

    private Command createSpecificationCommand() {
        Command specCmd = createCommand("", "specification");
        return specCmd;
    }

    private Command createCommand(String namespaceURI, String commandName) {
        return commandFactory.createCommand(namespaceURI, commandName);
    }
}

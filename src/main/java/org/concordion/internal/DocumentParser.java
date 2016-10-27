package org.concordion.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.*;

import org.concordion.api.*;
import org.concordion.api.Element;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.util.Check;

public class DocumentParser {

    private final CommandFactory commandFactory;
    private final List<DocumentParsingListener> listeners = new ArrayList<DocumentParsingListener>();
    
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
        CommandCall rootCommandCall = new CommandCall(null, createSpecificationCommand(), new Element(xomElement), "", resource);
        generateCommandCallTree(xomElement, rootCommandCall, resource);
        List<CommandCall> examples = new ArrayList<CommandCall>();
        List<CommandCall> beforeExamples = new ArrayList<CommandCall>();
        modifyCommandCallTree(rootCommandCall, examples, beforeExamples);
        return new XMLSpecification(rootCommandCall, examples, beforeExamples);
    }

    private void modifyCommandCallTree(CommandCall rootCommandCall, List<CommandCall> examples, List<CommandCall> beforeExamples) {

        rootCommandCall.modifyTree(examples, beforeExamples);

        List<CommandCall> childrenCopy = new ArrayList(rootCommandCall.getChildren().asCollection());

        for (CommandCall childCall: childrenCopy) {
            modifyCommandCallTree(childCall, examples, beforeExamples);
        }
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
                    commandCall = new CommandCall(parentCommandCall, command, new Element(xomElement), expression, resource);
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

package org.concordion.internal.parser.support;

import java.util.ArrayList;
import java.util.List;

public class ConcordionStatement {
    
    public Attribute command;
    public String text = "";
    public List<Attribute> attributes = new ArrayList<Attribute>();

    public ConcordionStatement(String commandName, String commandValue) {
        this.command = new Attribute(commandName, commandValue);
    }

    public ConcordionStatement withAttribute(String name, String value) {
        attributes.add(new Attribute(name, value));
        return this;
    }
    
    public ConcordionStatement withText(String text) {
        this.text = text;
        return this;
    }
}
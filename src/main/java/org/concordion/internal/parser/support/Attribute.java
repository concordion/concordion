package org.concordion.internal.parser.support;

import org.concordion.internal.util.SimpleFormatter;

public class Attribute {
    public final String name;
    public final String value;
    
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return SimpleFormatter.format("%s='%s\'", name, value);
    }
}
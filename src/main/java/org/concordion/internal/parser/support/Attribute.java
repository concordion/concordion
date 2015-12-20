package org.concordion.internal.parser.support;

public class Attribute {
    public final String name;
    public final String value;
    
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("%s='%s\'", name, value);
    }
}
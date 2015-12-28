package org.concordion.internal.parser.support;

import static org.junit.Assert.*;

import org.concordion.internal.parser.support.ConcordionStatement;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.junit.Test;

public class ConciseExpressionParserTest {
    private ConciseExpressionParser parser = new ConciseExpressionParser("c", "concordion");

    @Test
    public void shorthandSet() {
        ConcordionStatement statement = parser.parse("#a");
        assertEquals("concordion:set", statement.command.name);
        assertEquals("#a", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void shorthandAssertEquals() {
        ConcordionStatement statement = parser.parse("?=#x");
        assertEquals("concordion:assert-equals", statement.command.name);
        assertEquals("#x", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void shorthandAssertEqualsWithText() {
        ConcordionStatement statement = parser.parse("?=echo(#TEXT)", "abc");
        assertEquals("concordion:assert-equals", statement.command.name);
        assertEquals("echo(#TEXT)", statement.command.value);
        assertEquals("abc", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void shorthandExecute() {
        ConcordionStatement statement = parser.parse("foo()");
        assertEquals("concordion:execute", statement.command.name);
        assertEquals("foo()", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void shorthandExecuteWithText() {
        ConcordionStatement statement = parser.parse("foo(#TEXT)", "bar");
        assertEquals("concordion:execute", statement.command.name);
        assertEquals("foo(#TEXT)", statement.command.value);
        assertEquals("bar", statement.text);
        assertEquals(0, statement.attributes.size());
    }
    
    @Test
    public void shorthandExecuteAndSetWithText() {
        ConcordionStatement statement = parser.parse("#r=foo(#TEXT)", "baz");
        assertEquals("concordion:execute", statement.command.name);
        assertEquals("#r=foo(#TEXT)", statement.command.value);
        assertEquals("baz", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void nonShorthandCommand() {
        ConcordionStatement statement = parser.parse("c:command=value");
        assertEquals("concordion:command", statement.command.name);
        assertEquals("value", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void nonShorthandCommandWithAttribute() {
        ConcordionStatement statement = parser.parse("c:verifyRows=#book:listBooks() c:matchStrategy=BestMatch");
        assertEquals("concordion:verifyRows", statement.command.name);
        assertEquals("#book:listBooks()", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(1, statement.attributes.size());
        assertEquals("concordion:matchStrategy", statement.attributes.get(0).name);
        assertEquals("BestMatch", statement.attributes.get(0).value);
    }
    
    @Test
    public void commandValue() {
        ConcordionStatement statement = parser.parseCommandValueAndAttributes("example", "foo");
        assertEquals("concordion:example", statement.command.name);
        assertEquals("foo", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void commandValueMultiWord() {
        ConcordionStatement statement = parser.parseCommandValueAndAttributes("example", "foo bar");
        assertEquals("concordion:example", statement.command.name);
        assertEquals("foo bar", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(0, statement.attributes.size());
    }

    @Test
    public void commandValueWithAttribute() {
        ConcordionStatement statement = parser.parseCommandValueAndAttributes("example", "incomplete c:status=ExpectedToFail");
        assertEquals("concordion:example", statement.command.name);
        assertEquals("incomplete", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(1, statement.attributes.size());
        assertEquals("concordion:status", statement.attributes.get(0).name);
        assertEquals("ExpectedToFail", statement.attributes.get(0).value);
    }

    @Test
    public void commandValueMultiWordWithAttribute() {
        ConcordionStatement statement = parser.parseCommandValueAndAttributes("example", "foo bar c:status=Incomplete");
        assertEquals("concordion:example", statement.command.name);
        assertEquals("foo bar", statement.command.value);
        assertEquals("", statement.text);
        assertEquals(1, statement.attributes.size());
        assertEquals("concordion:status", statement.attributes.get(0).name);
        assertEquals("Incomplete", statement.attributes.get(0).value);
    }
}

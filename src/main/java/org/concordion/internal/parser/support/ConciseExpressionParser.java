package org.concordion.internal.parser.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.internal.util.SimpleFormatter;

/**
 * Parses the concise Concordion expression syntax into {@link ConcordionStatement}s.
 *
 * See <code>ConciseExpressionParserTest</code> for usage examples.
 */
public class ConciseExpressionParser {

    private static final Pattern COMMAND_VALUE_PATTERN = Pattern.compile("(.*?)(?:\\s+\\S+\\=\\S+\\s*)*");
    private final String sourcePrefix;
    private final String targetPrefix;

    public ConciseExpressionParser(String sourceConcordionNamespacePrefix, String targetConcordionNamespacePrefix) {
        this.sourcePrefix = sourceConcordionNamespacePrefix + ":";
        this.targetPrefix = targetConcordionNamespacePrefix + ":";
    }

    public ConcordionStatement parse(String expression) {
        return parse(expression, "");
    }

    public ConcordionStatement parse(String expression, String text) throws ConcordionSyntaxException {
        ConcordionStatement statement;
        if (expression.startsWith("#") && !(expression.contains("="))) {
            statement = new ConcordionStatement(targetPrefix + "set", expression);
        } else if (expression.startsWith("?=")) {
            statement = new ConcordionStatement(targetPrefix + "assert-equals", expression.substring(2));
        } else {
            if (expression.startsWith(sourcePrefix)) {
                statement = parseStatement(expression.substring(sourcePrefix.length()));
            } else {
                statement = new ConcordionStatement(targetPrefix + "execute", expression);
            }
        }
        return statement.withText(text);
    }

    private ConcordionStatement parseStatement(String statement) throws ConcordionSyntaxException {
        String[] components = statement.split("=", 2);
        String commandName = components[0];
        if (components.length != 2) {
            throw new ConcordionSyntaxException(SimpleFormatter.format("Invalid statement '%s'. Expected an = sign and a right hand side to the statement.", statement));
        }
        String valueAndAttributes = components[1];
        
        if (valueAndAttributes.contains("\"")) {
            throw new ConcordionSyntaxException(String.format("Invalid statement '%s'. Expected the right hand side to not contain double quotes.", statement));
        }
        
        return parseCommandValueAndAttributes(commandName, valueAndAttributes);
    }

    public ConcordionStatement parseCommandValueAndAttributes(String commandName, String commandValueAndAttributes) {
        Matcher commandValueMatcher = COMMAND_VALUE_PATTERN.matcher(commandValueAndAttributes);
        if (!commandValueMatcher.matches()) {
            throw new IllegalStateException(SimpleFormatter.format("Unexpected match failure for ''", commandValueAndAttributes));
        }
        
        String match = commandValueMatcher.group(1);
        String commandValue = match;
        ConcordionStatement statement = new ConcordionStatement(targetPrefix + commandName, commandValue);
        
        if (match.length() < commandValueAndAttributes.length()) {
            String attributesStr = commandValueAndAttributes.substring(match.length());
            String[] attributes = attributesStr.trim().split("\\s+");
            
            for (String attribute : attributes) {
                String[] parts = attribute.split("=", 2);
                String attributeName = parts[0];
                if (attributeName.startsWith(sourcePrefix)) {
                    attributeName = targetPrefix + attributeName.substring(sourcePrefix.length());
                }
                statement.withAttribute(attributeName, parts.length > 1 ? parts[1] : "");
            }
        }
        return statement;
    }
}

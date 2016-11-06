package org.concordion.internal.parser.support;

import java.util.Collections;
import java.util.Map;
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
    private Map<String, String> namespaces;

    public ConciseExpressionParser(String sourceConcordionNamespacePrefix, String targetConcordionNamespacePrefix) {
        this(sourceConcordionNamespacePrefix, targetConcordionNamespacePrefix, Collections.<String, String> emptyMap());
    }

    public ConciseExpressionParser(String sourceConcordionNamespacePrefix, String targetConcordionNamespacePrefix, Map<String, String> namespaces) {
        this.namespaces = namespaces;
        this.sourcePrefix = sourceConcordionNamespacePrefix + ":";
        this.targetPrefix = targetConcordionNamespacePrefix + ":";
    }

    public ConcordionStatement parse(String expression) {
        return parse(expression, "");
    }

    public ConcordionStatement parse(String expression, String text) throws ConcordionSyntaxException {
        ConcordionStatement statement;
        if (expression.equals("@")) {
            statement = new ConcordionStatement(targetPrefix + "example", "");
        } else if (expression.startsWith("#") && !(expression.contains("="))) {
            statement = new ConcordionStatement(targetPrefix + "set", expression);
        } else if (expression.startsWith("?=")) {
            statement = new ConcordionStatement(targetPrefix + "assert-equals", expression.substring(2));
        } else {
            if (expression.startsWith(sourcePrefix)) {
                statement = parseStatement(expression.substring(sourcePrefix.length()), true);
            } else {
                statement = createStatementFromNamespacePrefix(namespaces, expression);
                if (statement == null) {
                    statement = new ConcordionStatement(targetPrefix + "execute", expression);
                }
            }
        }
        return statement.withText(text);
    }

    private ConcordionStatement createStatementFromNamespacePrefix(Map<String, String> namespaces, String expression) {
        for (String prefix : namespaces.keySet()) {
            if (expression.startsWith(prefix + ":")) {
                return parseStatement(expression, false);
            }
        }
        return null;
    }

    private ConcordionStatement parseStatement(String statement, boolean includeConcordionPrefix) throws ConcordionSyntaxException {
        String[] components = statement.split("=", 2);
        String commandName = components[0];
        String valueAndAttributes;
        if (components.length == 2) {
            valueAndAttributes = components[1];
        } else if (components.length == 1 && commandName.equals("example")) {
            valueAndAttributes = "";
        } else {
            throw new ConcordionSyntaxException(SimpleFormatter.format("Invalid statement '%s'. Expected an = sign and a right hand side to the statement.", statement));
        }

        if (valueAndAttributes.contains("\"")) {
            throw new ConcordionSyntaxException(String.format("Invalid statement '%s'. Expected the right hand side to not contain double quotes.", statement));
        }

        return parseCommandValueAndAttributes(commandName, valueAndAttributes, includeConcordionPrefix);
    }

    public ConcordionStatement parseCommandValueAndAttributes(String commandName, String commandValueAndAttributes) {
        return parseCommandValueAndAttributes(commandName, commandValueAndAttributes, true);
    }

    public ConcordionStatement parseCommandValueAndAttributes(String commandName, String commandValueAndAttributes, boolean includeConcordionPrefix) {
        Matcher commandValueMatcher = COMMAND_VALUE_PATTERN.matcher(commandValueAndAttributes);
        if (!commandValueMatcher.matches()) {
            throw new IllegalStateException(SimpleFormatter.format("Unexpected match failure for ''", commandValueAndAttributes));
        }

        String match = commandValueMatcher.group(1);
        String commandValue = match;
        ConcordionStatement statement = new ConcordionStatement((includeConcordionPrefix ? targetPrefix : "") + commandName, commandValue);

        if (match.length() < commandValueAndAttributes.length()) {
            String attributesStr = commandValueAndAttributes.substring(match.length());
            String[] attributes = attributesStr.trim().split("\\s+");

            for (String attribute : attributes) {
                String[] parts = attribute.split("=", 2);
                String attributeName = parts[0];
                if (attributeName.startsWith(sourcePrefix)) {
                    attributeName = (includeConcordionPrefix ? targetPrefix : "") + attributeName.substring(sourcePrefix.length());
                }
                statement.withAttribute(attributeName, parts.length > 1 ? parts[1] : "");
            }
        }
        return statement;
    }

    public boolean isExecuteCommand(ConcordionStatement commandStatement) {
        return commandStatement.command.name.equals(targetPrefix + "execute");
    }
}

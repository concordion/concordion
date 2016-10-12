package org.concordion.internal.parser.markdown;

import java.util.List;
import java.util.Map;

import org.concordion.internal.parser.support.Attribute;
import org.concordion.internal.parser.support.ConciseExpressionParser;
import org.concordion.internal.parser.support.ConcordionStatement;
import org.concordion.internal.util.SimpleFormatter;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.*;

public class ConcordionHtmlSerializer extends ToHtmlSerializer {
    private static final String URL_FOR_CONCORDION = "-";
    private static final String SOURCE_CONCORDION_NAMESPACE_PREFIX = "c";
    private final ConciseExpressionParser statementParser; 
    
    private ConcordionStatement pendingCommand = null;
    private boolean inHeaderNode;
    private boolean inExample;
    private String currentExampleHeading;
    private int currentExampleLevel;
    private int depth;
    private boolean escapeHtmlNodes;
    private boolean escapeText;

    public ConcordionHtmlSerializer(String targetConcordionNamespacePrefix, Map<String, String> namespaces) {
        super(new RunCommandLinkRenderer(SOURCE_CONCORDION_NAMESPACE_PREFIX, targetConcordionNamespacePrefix));
        statementParser = new ConciseExpressionParser(SOURCE_CONCORDION_NAMESPACE_PREFIX, targetConcordionNamespacePrefix, namespaces);
    }
   
//=======================================================================================================================
// 
    @Override
    public void visit(ExpLinkNode node) {
        if (URL_FOR_CONCORDION.equals(node.url)) {
            LinkNode linkNode = toLinkNode(node);
            visit(linkNode);
        } else {
            super.visit(node);
        }
    }

    @Override
    public void visit(RefLinkNode node) {
        LinkNode linkNode = toLinkNode(node);
        if (URL_FOR_CONCORDION.equals(linkNode.getUrl())) {
            visit(linkNode);
        } else {
            super.visit(node);
        }
    }

    private LinkNode toLinkNode(RefLinkNode node) {
        escapeHtmlNodes = true;
        String text = printChildrenToString(node);
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text;
        escapeHtmlNodes = false;
        ReferenceNode refNode = references.get(normalize(key));
        String title = null;
        String url = null;
        if (refNode != null) {
            title = refNode.getTitle();
            url = refNode.getUrl();
        }
        LinkNode linkNode = new LinkNode(url, title, text);
        return linkNode;
    }

    private LinkNode toLinkNode(ExpLinkNode node) {
        if (!inHeaderNode) {
            escapeHtmlNodes = true;
        }
        String text = printChildrenToString(node);
        escapeHtmlNodes = false;
        return new LinkNode(node.url, node.title, text);
    }
    
    private void visit(LinkNode linkNode) {
        // Some commands only require an expression and don't need a text value to be passed. However, Markdown links always require link text.
        // Any text value that starts with italics will be set to an empty text value.
        String text = linkNode.getText();
        if (text.startsWith("<em>")) {
            text = "";
        };
        if (inHeaderNode || inTableHeader) {
            printer.print(text);
        } else {
            String expression = linkNode.getTitle();
            if (expression.equals("c:run")) {
                throw new ConcordionMarkdownException("Markdown link contains invalid URL for \"c:run\" command.\n"
                        + "Set the URL to the location of the specification to be run, rather than '-'.\n"
                        + "For example, [My Specification](mySpec.md \"c:run\")");
            }
            ConcordionStatement command = statementParser.parse(expression, text);
            printConcordionCommandElement(command);
        }
    }
//=======================================================================================================================
// concordion:execute on a table and concordion:verifyRows
// The concordion:execute command is in the first (and only) cell of the first header row.
// The header row containing the command has to be removed, and the command needs to be printed on the table row.    
    
    @Override
    public void visit(TableNode tableNode) {
        if (firstChildIsInstanceOf(tableNode, TableHeaderNode.class)) {
            Node header = firstChildOf(tableNode);
            if (firstChildIsInstanceOf(header, TableRowNode.class)) {
                Node row = firstChildOf(header);
                if (firstChildIsInstanceOf(row, TableCellNode.class)) {
                    Node cell = firstChildOf(row);
                    LinkNode linkNode = null;
                    if (firstChildIsInstanceOf(cell, ExpLinkNode.class)) {
                        linkNode = toLinkNode((ExpLinkNode) firstChildOf(cell));
                    } else if (firstChildIsInstanceOf(cell, RefLinkNode.class)) {
                        linkNode = toLinkNode((RefLinkNode) firstChildOf(cell));
                    }
                    if (linkNode != null) {
                        if (linkNode.getTitle() == null) {
                            throw new IllegalStateException(SimpleFormatter.format("No title found for link node '%s'", linkNode.getText()));
                        }
                        pendingCommand = statementParser.parse(linkNode.getTitle(), linkNode.getText());
                        cell.getChildren().remove(0);
                        if (linkNode.getText().startsWith("@")) {
                            cell.getChildren().add(new ExpLinkNode("c:example=", URL_FOR_CONCORDION, new TextNode(linkNode.getText().substring(1))));
                        }
                    }
                }
            }
        }

        // Call the super visit(TableNode) method and override printIndentedTag() below, so that the concordion command is added to the <table> tag.
        super.visit(tableNode);
    }

    @Override
    protected void printIndentedTag(SuperNode node, String tag) {
        printer.println().print('<').print(tag);
        printPendingConcordionCommand();
        printer.print('>').indent(+2);
        visitChildren(node);
        printer.indent(-2).println().print('<').print('/').print(tag).print('>');
    }
    
//-----------------------------------------------------------------------------------------------------------------------
// The concordion:set and concordion:assertEquals commands have to be on the TableColumnNode <th> tags, rather than child ExpLinkNodes.     
    @Override
    public void visit(TableCellNode node) {
        if (inTableHeader) {
            for (Node child : node.getChildren()) {
                String title;
                if (child instanceof ExpLinkNode) {
                    ExpLinkNode linkNode = (ExpLinkNode) child;
                    title = linkNode.title;
                    if (title == null) {
                        throw new IllegalStateException(String.format("No title found for link node with url '%s'", linkNode.url));
                    }
                    pendingCommand = statementParser.parse(title, "");
                } else if (child instanceof RefLinkNode) {
                    LinkNode linkNode = toLinkNode((RefLinkNode) child);
                    title = linkNode.getTitle();
                    if (title == null) {
                        throw new IllegalStateException(String.format("No title found for link node with reference '%s'. This can be caused by the associated link text being empty.", linkNode.getText()));
                    }
                    pendingCommand = statementParser.parse(title, "");
                }
            }
        }
        
        // Call the super visit(TableCellNode) method and override visit(TableColumnNode) below, so that the concordion commands are added to the <th> tag
        escapeHtmlNodes = true;
        super.visit(node);
        escapeHtmlNodes = false;
    }

    public void visit(TableColumnNode node) {
        super.visit(node);
        printPendingConcordionCommand();
    }

//=======================================================================================================================
// concordion:example support
    public void visit(HeaderNode node) {
        inHeaderNode = true;
        boolean printHeaderNode = true;
        for (Node child : node.getChildren()) {
            if (child instanceof ExpLinkNode) {
                if (URL_FOR_CONCORDION.equals(((ExpLinkNode) child).url)) {
                    closeExampleIfNeedeed();
                    String expression = ((ExpLinkNode)child).title;
                    currentExampleHeading = printChildrenToString(node);
                    currentExampleLevel = node.getLevel();
                    String exampleName = toExampleName(expression);
                    ConcordionStatement command = statementParser.parseCommandValueAndAttributes("example", exampleName, true);
                    printer.println();
                    printer.print("<div");
                    printConcordionCommand(command);
                    printer.print(">");
                    inExample = true;
                }
            } else if (inExample) {
                if (child instanceof StrikeNode) {
                    String exampleHeading = printChildrenToString(node).replace("<del>", "").replace("</del>", "");
                    if (currentExampleHeading != null && currentExampleHeading.equals(exampleHeading)) {
                        closeExample();
                        printHeaderNode = false;
                    }
                } else {
                    if (node.getLevel() < currentExampleLevel) {
                        closeExample();
                    }
                }
            }
        }
        if (printHeaderNode) {
            escapeText = true;
            escapeHtmlNodes = true;
            super.visit(node);
            escapeHtmlNodes = false;
            escapeText = false;
        }
        inHeaderNode = false;
    }

    public void visit(RootNode node) {
        depth++;
        super.visit(node);
        depth--;
        // Use a counter to check it is the root node, since we are getting nested RootNodes in the AST when there is a list in the Markdown
        if (depth == 0) {
            closeExampleIfNeedeed();
        }
    }

    private void closeExampleIfNeedeed() {
        if (inExample) {
            closeExample();
        }
    }

    private void closeExample() {
        printer.print("</div>");
        inExample = false;
        currentExampleHeading = "";
        currentExampleLevel = 0;
    }
    
    private String toExampleName(String expression) {
        String exampleName = expression;
        if (exampleName == null || exampleName.trim().isEmpty()) {
            exampleName = generateName(currentExampleHeading);
        } else {
            exampleName = exampleName.trim();
        }
        return exampleName;
    }
    
    private String generateName(String headingText) {
        StringBuilder sb = new StringBuilder(headingText.length());
        for (char c : headingText.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '-') {
                sb.append('-');
            }
        }
        String name = sb.toString();
        if (name.endsWith("-")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

//=======================================================================================================================
// support methods    
    private void printConcordionCommandElement(ConcordionStatement command) {
        printer.print('<').print("span");
        printConcordionCommand(command);
        printer.print('>');
        printer.print(command.text);
        printer.print('<').print('/').print("span").print('>');
    }

    private void printPendingConcordionCommand() {
        if (pendingCommand != null) {
            printConcordionCommand(pendingCommand);
            pendingCommand = null;
        }
    }

    private void printConcordionCommand(ConcordionStatement command) {
        printAttribute(command.command.name, command.command.value);
        
        List<Attribute> attributes = command.attributes;
        for (Attribute attribute : attributes) {
            printAttribute(attribute.name, attribute.value);
        }
    }
    
    private Node firstChildOf(Node node) {
        return node.getChildren().get(0);
    }

    private boolean firstChildIsInstanceOf(Node node, Class<?> clazz) {
        return node.getChildren().size() > 0 && clazz.isAssignableFrom(firstChildOf(node).getClass());
    }

    public void visit(InlineHtmlNode node) {
        if (escapeHtmlNodes) {
            this.printer.printEncoded(node.getText());
        } else {
            super.visit(node);
        }
    }

    public void visit(TextNode node) {
        if (escapeText) {
            this.printer.printEncoded(node.getText());
        } else {
            super.visit(node);
        }
    }
}
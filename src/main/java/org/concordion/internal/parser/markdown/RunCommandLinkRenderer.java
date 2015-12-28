package org.concordion.internal.parser.markdown;

import org.pegdown.LinkRenderer;
import org.pegdown.ast.ExpLinkNode;

public class RunCommandLinkRenderer extends LinkRenderer {
    private String concordionNamespacePrefix;
    private String runCommandToken;

    public RunCommandLinkRenderer(String sourceConcordionNamespacePrefix, String targetConcordionNamespacePrefix) {
        runCommandToken = sourceConcordionNamespacePrefix + ":run";
        this.concordionNamespacePrefix = targetConcordionNamespacePrefix;
    }

    @Override
    public Rendering render(ExpLinkNode node, String text) {
        if (node.title.startsWith(runCommandToken)) {
            Rendering rendering = new Rendering(node.url, text);
            String suffix = node.title.substring(runCommandToken.length()).trim();
            String runner;
            if (suffix.startsWith("=")) {
                runner = suffix.substring(1).trim();
                if (runner.startsWith("'") || runner.startsWith("\"")) {
                    runner = runner.substring(1, runner.length() - 1);
                }
            } else {
                runner = "concordion";
            }
            
            return rendering.withAttribute(concordionNamespacePrefix + ":run", runner);
        } 
        return super.render(node, text);
    }
}
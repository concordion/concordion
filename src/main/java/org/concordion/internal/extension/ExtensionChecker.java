package org.concordion.internal.extension;

import org.concordion.internal.ConfigurationException;


public class ExtensionChecker {

    private static final String MARKDOWN_EXTENSION_CLASS = "org.concordion.ext.MarkdownExtension";
    private static final String EXTENSIONS_CLASS = "org.concordion.ext.Extensions";

    public static void checkForOutdatedExtensions() {
        checkForExtensionsNowInCore();
        checkForDeprecatedExtensions();
    }

    private static void checkForExtensionsNowInCore() {
        try {
            Class.forName(MARKDOWN_EXTENSION_CLASS);
        } catch (ClassNotFoundException expected) {
            return; // We don't want it to be found
        }
        String msg = "The Markdown format is now supported in the main Concordion module. The concordion-markdown-extension module must be removed from the class path.";
        throw new ConfigurationException(msg);
    }

    private static void checkForDeprecatedExtensions() {
        try {
            Class.forName(EXTENSIONS_CLASS);
        } catch (ClassNotFoundException expected) {
            return; // We don't want it to be found
        }
        String msg = "The concordion-extensions module is now deprecated. Please replace with the individual extension modules. See http://concordion.org/Extensions.html.";
        System.err.println(msg);
    }
}

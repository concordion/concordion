package org.concordion.internal.extension;

import org.concordion.internal.ConfigurationException;


public class ExtensionChecker {

    private static final String MARKDOWN_EXTENSION_CLASS = "org.concordion.ext.MarkdownExtension";
    private static final String EXTENSIONS_CLASS = "org.concordion.ext.Extensions";
    private static final String EXCEL_EXTENSION_SOURCE_CLASS = "org.concordion.ext.excel.ExcelClassPathSource";
    private static final String RUN_TOTALS_EXTENSION_CLASS = "org.concordion.ext.runtotals.RunTotalsExtension";
    private static final String EXECUTE_ONLY_IF_EXTENSION_CLASS = "org.concordion.ext.ExecuteOnlyIfExtension";

    public static void checkForOutdatedExtensions() {
        checkForExtensionsNowInCore();
        checkForBreakingChanges();
        checkCompatibility();
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

    private static void checkForBreakingChanges() {
        try {
            Class.forName(EXCEL_EXTENSION_SOURCE_CLASS);
        } catch (ClassNotFoundException expected) {
            return; // We don't want it to be found
        }
        String msg = "The Concordion Excel Extension must be updated to the latest version to work with Concordion 2.0.0 or later.";
        throw new ConfigurationException(msg);
    }

    private static void checkCompatibility() {
        assertCompatibleVersion(RUN_TOTALS_EXTENSION_CLASS, "concordion-run-totals-extension", "1.2", "2.2.0");
        assertCompatibleVersion(EXECUTE_ONLY_IF_EXTENSION_CLASS, "execute-executeonlyif-extension", "0.3", "2.2.0");
    }

    private static void assertCompatibleVersion(String extensionClassName, String extensionName, String requiredSpecificationVersion, String breakingConcordionVersion) {
        try {
            Class<?> extensionClass = Class.forName(extensionClassName);
            Package extensionPackage = extensionClass.getPackage();
            String specificationVersion = extensionPackage.getSpecificationVersion();
            if (extensionPackage.getImplementationTitle() != null) {
                extensionName = extensionPackage.getImplementationTitle();
            }
            if (specificationVersion == null || !extensionPackage.isCompatibleWith(requiredSpecificationVersion)) {
                String msg = "The " + extensionName + " must be updated to version " + requiredSpecificationVersion +
                        ".0 or later to work with Concordion " + breakingConcordionVersion + " or later.";
                throw new ConfigurationException(msg);
            }
            return; // Passes the check since it's the right version
        } catch (ClassNotFoundException expected) {
            return; // Passes the check since it's not on classpath
        }
    }

    private static void checkForDeprecatedExtensions() {
        try {
            Class.forName(EXTENSIONS_CLASS);
        } catch (ClassNotFoundException expected) {
            return; // We don't want it to be found
        }
        String msg = "Warning. The concordion-extensions module is now deprecated. Please replace with the individual extension modules. See https://concordion.org/Extensions.html.";
        System.err.println(msg);
    }
}

package org.concordion.internal.extension;

import org.concordion.internal.ConfigurationException;


public class ExtensionChecker {

    private static final String EMBED_EXTENSION_CLASS           = "org.concordion.ext.embed.EmbedCommand";
    private static final String EXCEL_EXTENSION_CLASS           = "org.concordion.ext.excel.ExcelExtension";
    private static final String EXECUTE_ONLY_IF_EXTENSION_CLASS = "org.concordion.ext.executeOnlyIf.ExecuteOnlyIfCommand";
    private static final String RUN_TOTALS_EXTENSION_CLASS      = "org.concordion.ext.runtotals.RunTotalsExtension";
    private static final String SCREENSHOT_EXTENSION_CLASS      = "org.concordion.ext.screenshot.ScreenshotEmbedder";

    private static final String OUTDATED_EXCEL_EXTENSION_CLASS  = "org.concordion.ext.excel.ExcelClassPathSource";
    private static final String OUTDATED_EXTENSIONS_CLASS       = "org.concordion.ext.Extensions";
    private static final String OUTDATED_MARKDOWN_EXTENSION_CLASS = "org.concordion.ext.MarkdownExtension";

    private static boolean checked = false;

    public static void checkForOutdatedExtensions() {
        if (!checked) {
            failIfOutdatedExtensionFound();
            checkExtensionCompatibility();
        }
        checked = true;
    }

    private static void failIfOutdatedExtensionFound() {
        failIfPresent(OUTDATED_MARKDOWN_EXTENSION_CLASS, "The Markdown format is now supported in the main Concordion module. The concordion-markdown-extension module must be removed from the class path.");
        failIfPresent(OUTDATED_EXTENSIONS_CLASS, "Warning. The concordion-extensions module is now deprecated. Please replace with the individual extension modules. See https://concordion.org/Extensions.html.");
        failIfPresent(OUTDATED_EXCEL_EXTENSION_CLASS, "The Concordion Excel Extension must be updated to the latest version to work with Concordion 2.0.0 or later.");
    }

    private static void checkExtensionCompatibility() {
        failIfIncompatible(EXECUTE_ONLY_IF_EXTENSION_CLASS, "concordion-executeonlyif-extension", "0.3", "2.2.0");
        failIfIncompatible(RUN_TOTALS_EXTENSION_CLASS, "concordion-run-totals-extension", "1.2", "2.2.0");
        warnIfIncompatible(EMBED_EXTENSION_CLASS, "concordion-embed-extension", "1.2");
        warnIfIncompatible(EXCEL_EXTENSION_CLASS, "concordion-excel-extension", "2.1");
        warnIfIncompatible(SCREENSHOT_EXTENSION_CLASS, "concordion-screenshot-extension", "1.3");
    }

    private static void failIfPresent(String outdatedExtensionClass, String msg) {
        try {
            Class.forName(outdatedExtensionClass);
        } catch (ClassNotFoundException expected) {
            return; // We don't want it to be found
        }
        throw new ConfigurationException(msg);
    }

    private static void warnIfIncompatible(String extensionClassName, String extensionName, String requiredSpecificationVersion) {
        if (!isCompatible(extensionClassName, requiredSpecificationVersion)) {
            String msg = "Warning: This version of " + extensionName + " is deprecated. " +
                    "Please update to version " + requiredSpecificationVersion + ".0.";
            System.err.println(msg);
        }
    }

    private static void failIfIncompatible(String extensionClassName, String extensionName, String requiredSpecificationVersion, String breakingConcordionVersion) {
        if (!isCompatible(extensionClassName, requiredSpecificationVersion)) {
            String msg = "The " + extensionName + " must be updated to version " + requiredSpecificationVersion +
                    ".0 or later to work with Concordion " + breakingConcordionVersion + " or later.";
            throw new ConfigurationException(msg);
        }
    }

    private static boolean isCompatible(String extensionClassName, String requiredSpecificationVersion) {
        boolean compatible;
        try {
            Class<?> extensionClass = Class.forName(extensionClassName);
            Package extensionPackage = extensionClass.getPackage();
            String specificationVersion = extensionPackage.getSpecificationVersion();
            compatible = specificationVersion != null && extensionPackage.isCompatibleWith(requiredSpecificationVersion);
        } catch (ClassNotFoundException expected) {
            compatible = true;  // Passes the check since it's not on classpath
        }
        return compatible;
    }
}

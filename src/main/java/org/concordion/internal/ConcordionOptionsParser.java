package org.concordion.internal;

import java.util.HashMap;
import java.util.Map;

public class ConcordionOptionsParser {

    public static Map<String, String> convertNamespacePairsToMap(String[] namespacePairs) {
        if (namespacePairs.length % 2 == 1) {
            throw new ConfigurationException("The declareNamespaces element of @ConcordionOptions must include an even number of arguments, "
                    + "alternating between a namespace prefix and the namespace it maps to");
        }
        HashMap<String, String> namespaces = new HashMap<String, String>(namespacePairs.length / 2);
        for (int i = 0; i < namespacePairs.length; i+=2) {
            namespaces.put(namespacePairs[i], namespacePairs[i+1]);
        }
        return namespaces;
    }
}

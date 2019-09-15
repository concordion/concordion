package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataValueFactory;

import java.util.Collections;
import java.util.Map;

public class ConcordionMarkdownOptions {
    public static final String URL_FOR_CONCORDION = "-";
    public static final String SOURCE_CONCORDION_NAMESPACE_PREFIX = "c";
    public static final DataKey<Map<String, String>> CONCORDION_ADDITIONAL_NAMESPACES = new DataKey<Map<String, String>>("CONCORDION_ADDITIONAL_NAMESPACES",
            new DataValueFactory<Map<String, String>>() {
                @Override
                public Map<String, String> apply(DataHolder value) {
                    return Collections.EMPTY_MAP;
                }
            });
    public static final DataKey<String> CONCORDION_TARGET_NAMESPACE = new DataKey<String>("CONCORDION_TARGET_NAMESPACE", "concordion");
}

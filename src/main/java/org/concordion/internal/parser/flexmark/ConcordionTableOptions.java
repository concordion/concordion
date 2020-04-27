package org.concordion.internal.parser.flexmark;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.data.DataHolder;

public class ConcordionTableOptions {
    public final String className;
    public final Boolean columnSpans;

    public ConcordionTableOptions(DataHolder options) {
        this.className = TablesExtension.CLASS_NAME.getFrom(options);
        this.columnSpans = TablesExtension.COLUMN_SPANS.getFrom(options);
    }
}

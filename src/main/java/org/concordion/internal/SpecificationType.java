package org.concordion.internal;

import org.concordion.api.SpecificationConverter;

public class SpecificationType {

    private final String typeSuffix;
    private final SpecificationConverter converter;

    public SpecificationType(String typeSuffix, SpecificationConverter converter) {
        this.typeSuffix = typeSuffix;
        this.converter = converter;
    }

    public String getTypeSuffix() {
        return typeSuffix;
    }

    public SpecificationConverter getConverter() {
        return converter;
    }
}

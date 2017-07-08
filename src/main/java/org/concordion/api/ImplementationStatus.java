package org.concordion.api;

import java.lang.annotation.Annotation;

public enum ImplementationStatus {
    UNIMPLEMENTED("Unimplemented", Unimplemented.class),
    EXPECTED_TO_FAIL("ExpectedToFail", ExpectedToFail.class),
    EXPECTED_TO_PASS("ExpectedToPass", ExpectedToPass.class);

    private final String tag;
    private final Class<? extends Annotation> annotation;

    ImplementationStatus(String tag, Class<? extends Annotation> annotation) {
        this.tag = tag;
        this.annotation = annotation;
    }

    public String getTag() {
        return tag;
    }
    
    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public static ImplementationStatus implementationStatusFor(String tag) {
        for (ImplementationStatus implementationStatus : values()) {
            if (implementationStatus.getTag().equalsIgnoreCase(tag)) {
                return implementationStatus;
            }
        }

        throw new IllegalArgumentException("No implementation status for " + tag);
    }
}

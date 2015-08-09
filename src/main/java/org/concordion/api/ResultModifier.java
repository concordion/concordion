package org.concordion.api;

import java.lang.annotation.Annotation;

/**
 * Created by tim on 7/07/15.
 */
public enum ResultModifier {
    UNIMPLEMENTED("Unimplemented", Unimplemented.class),
    EXPECTED_TO_FAIL("ExpectedToFail", ExpectedToFail.class),
    EXPECTED_TO_PASS("ExpectedToPass", ExpectedToPass.class);

    private final Class<? extends Annotation> annotation;
    private final String tag;

    ResultModifier(String tag, Class<? extends Annotation> annotation) {
        this.tag = tag;
        this.annotation = annotation;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public String getTag() {
        return tag;
    }

    public static ResultModifier getModifier(String tag) {
        for (ResultModifier resultModifier: values()) {
            if (resultModifier.getTag().equalsIgnoreCase(tag)) {
                return resultModifier;
            }
        }
        throw new IllegalArgumentException("No result modifier for " + tag);
    }
}

package org.concordion.integration.junit.platform.engine;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.TestFrameworkProvider;
import org.junit.platform.commons.support.AnnotationSupport;

public class JUnit5FrameworkProvider implements TestFrameworkProvider {

    @Override
    public boolean isConcordionFixture(Class<?> clazz) {
        return AnnotationSupport.findAnnotation(
                clazz, ConcordionFixture.class).isPresent();
    }

}

package org.concordion.integration.junit4;

import org.concordion.integration.TestFrameworkProvider;
import org.junit.runner.RunWith;

public class JUnit4FrameworkProvider implements TestFrameworkProvider {
    @Override
    public boolean isConcordionFixture(Class<?> clazz) {
        RunWith annotation = clazz.getAnnotation(RunWith.class);
        return annotation != null && ConcordionRunner.class.isAssignableFrom(annotation.value());
    }
}

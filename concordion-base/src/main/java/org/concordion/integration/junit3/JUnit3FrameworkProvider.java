package org.concordion.integration.junit3;

import org.concordion.integration.TestFrameworkProvider;

public class JUnit3FrameworkProvider implements TestFrameworkProvider {
    @Override
    public boolean isConcordionFixture(Class<?> clazz) {
        return ConcordionTestCase.class.isAssignableFrom(clazz);
    }
}

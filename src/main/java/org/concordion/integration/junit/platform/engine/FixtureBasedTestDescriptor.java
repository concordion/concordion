package org.concordion.integration.junit.platform.engine;

import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.FixtureSpecificationMapper;
import org.concordion.internal.FixtureType;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

/**
 * Abstract fixture-based implementation of {@link TestDescriptor}.
 *
 * @since 4.0
 */
public abstract class FixtureBasedTestDescriptor extends AbstractTestDescriptor {

    private final Class<?> fixtureClass;

    FixtureBasedTestDescriptor(UniqueId uniqueId, Class<?> fixtureClass) {
        this(uniqueId,
                fixtureClass,
                FixtureSpecificationMapper
                        .removeSuffixFromFixtureName(fixtureClass.getName()));
    }

    FixtureBasedTestDescriptor(
            UniqueId uniqueId, Class<?> fixtureClass, String displayName) {
        this(uniqueId,
                fixtureClass,
                displayName,
                ClassSource.from(fixtureClass));
    }

    FixtureBasedTestDescriptor(
            UniqueId uniqueId, Class<?> fixtureClass, String displayName, TestSource source) {
        super(uniqueId, displayName, source);
        this.fixtureClass = fixtureClass;
    }

    public Class<?> getFixtureClass() {
        return fixtureClass;
    }

    public abstract FixtureType getFixtureType();

    public abstract FixtureInstance getFixtureInstance();

    public abstract FixtureRunner getFixtureRunner();

}

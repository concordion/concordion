package org.concordion.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks the class (with a class name that ends with <code>Fixture</code> or
 * <code>Test</code>) as a fixture for a specification.
 * <p>
 * This allows it to be discovered by
 * {@link org.concordion.integration.junit.platform.engine.ConcordionTestEngine
 * Concordion's JUnit Platform TestEngine}.
 * </p>
 * <p>
 * This <em>must not</em> be used with JUnit Jupiter's
 * {@literal @}{@link org.junit.jupiter.api.extension.ExtendWith
 * ExtendWith}.
 * </p>
 *
 * @since 4.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Testable
public @interface ConcordionFixture {
}

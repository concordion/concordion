package org.concordion.integration.junit.platform.engine;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.SpecificationLocator;
import org.concordion.internal.ClassNameBasedSpecificationLocator;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

/**
 * The Concordion {@link org.junit.platform.engine.TestEngine TestEngine}.
 * <p>
 * Supports discovery and execution of <em>specifications</em> through
 * {@link ConcordionFixture fixtures} using the following selectors:
 * </p>
 * <ul>
 * <li>{@link ClassSelector}</li>
 * <li>{@link PackageSelector}</li>
 * </ul>
 *
 * @see ConcordionFixture
 * @see SpecificationDescriptor
 * @see ExampleDescriptor
 * @since 4.0
 */
public class ConcordionTestEngine extends HierarchicalTestEngine<ConcordionEngineExecutionContext> {

    public static final String ENGINE_ID = "concordion";

    public static final String ENDS_WITH_FIXTURE_OR_TEST_REGEX = ".*(Fixture|Test)$";
    private static final Predicate<Class<?>> IS_FIXTURE_CLASS = (clazz) -> {
            return AnnotationSupport.findAnnotation(
                    clazz, ConcordionFixture.class).isPresent()
                    && clazz.getName().matches(ENDS_WITH_FIXTURE_OR_TEST_REGEX);
        };

    /**
     * Cache specificationDescriptors to avoid specifications being loaded multiple times
     * and fixture classes being loaded multiple times when a suite of tests is run
     * (and the run command is used to run specifications from other specifications).
     */
    private static Map<Class<?>, SpecificationDescriptor> specificationDescriptorCache = new ConcurrentHashMap<Class<?>, SpecificationDescriptor>();

    public static void _clearCacheForTestingOnly() {
        specificationDescriptorCache.clear();
    }

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        TestDescriptor rootTestDescriptor = new ConcordionEngineDescriptor(uniqueId, "Concordion for JUnit Platform");
        SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();

        // Include fixtures with @org.concordion.api.ConcordionFixture annotation
        final Predicate<Class<?>> predicate = IS_FIXTURE_CLASS;

        // @formatter:off
        discoveryRequest.getSelectorsByType(ClassSelector.class)
                .stream()
                // .peek(selector -> System.out.println(selector.getJavaClass().getName()))
                .map(selector -> selector.getJavaClass())
                .filter(predicate)
                // .peek(clazz -> System.out.println(clazz.getName()))
                .forEach(fixtureClass -> {
                    appendFixtureBasedTestDescriptor(
                            fixtureClass, specificationLocator, rootTestDescriptor);
                });
        discoveryRequest.getSelectorsByType(PackageSelector.class)
                .stream()
                // .peek(selector -> System.out.println(selector.getPackageName()))
                .map(selector -> ReflectionSupport.findAllClassesInPackage(
                            selector.getPackageName(), predicate, className -> true))
                .flatMap(List::stream)
                // .peek(clazz -> System.out.println(clazz.getName()))
                .forEach(fixtureClass -> {
                    appendFixtureBasedTestDescriptor(
                            fixtureClass, specificationLocator, rootTestDescriptor);
                });
        // @formatter:on

        return rootTestDescriptor;
    }

    protected void appendFixtureBasedTestDescriptor(
            Class<?> fixtureClass, SpecificationLocator specificationLocator, TestDescriptor parentTestDescriptor) {
        try {
            // Create a SpecificationTestDescriptor and append to parent
            SpecificationDescriptor specificationTestDescriptor =
                    appendSpecificationTestDescriptor(fixtureClass, specificationLocator, parentTestDescriptor);
            List<String> exampleNames = specificationTestDescriptor.getExampleNames();
            // System.out.println(exampleNames);
            for (String exampleName : exampleNames) {
                // Create an ExampleTestDescriptor and append to SpecificationTestDescriptor
                appendExampleTestDescriptor(exampleName, fixtureClass, specificationTestDescriptor);
            }
        } catch (IOException ex) {
            // System.out.println(fixtureClass);
            throw new RuntimeException(
                    "Error occured while loading specification examples (with [" + fixtureClass.getName() + "] fixture)", ex);
        }
    }

    protected synchronized SpecificationDescriptor appendSpecificationTestDescriptor(
            Class<?> fixtureClass, SpecificationLocator specificationLocator, TestDescriptor parentTestDescriptor) {
        SpecificationDescriptor specificationDescriptor = specificationDescriptorCache.get(fixtureClass);
        if (specificationDescriptor == null) {
            specificationDescriptor = new SpecificationDescriptor(
                    parentTestDescriptor.getUniqueId(),
                    fixtureClass, specificationLocator);
            specificationDescriptorCache.put(fixtureClass, specificationDescriptor);
        }
        // System.out.println("Added " + newTestDescriptor.getClass().getSimpleName() + " for " + newTestDescriptor.getFixtureClass().getSimpleName());
        parentTestDescriptor.addChild(specificationDescriptor);
        return specificationDescriptor;
    }

    protected ExampleDescriptor appendExampleTestDescriptor(
            String exampleName, Class<?> fixtureClass, TestDescriptor parentTestDescriptor) {
        ExampleDescriptor newTestDescriptor = new ExampleDescriptor(
                parentTestDescriptor.getUniqueId().append(
                        ExampleDescriptor.SEGMENT_TYPE, exampleName),
                fixtureClass,
                exampleName);
        // System.out.println("Added " + newTestDescriptor.getClass().getSimpleName() + " for " + newTestDescriptor.getExampleName());
        parentTestDescriptor.addChild(newTestDescriptor);
        return newTestDescriptor;
    }

    @Override
    protected ConcordionEngineExecutionContext createExecutionContext(ExecutionRequest request) {
        return new ConcordionEngineExecutionContext(request);
    }

}

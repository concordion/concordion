package org.concordion.integration.junit.platform.engine;

import org.concordion.internal.FixtureType;
import org.concordion.internal.cache.RunResultsCache;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import spec.examples.DemoTest;
import spec.examples.PartialMatchesTest;
import spec.examples.SpikeTest;

import static org.junit.jupiter.api.Assertions.*;

class ConcordionTestEngineTests {

    @Test
    void id() throws Exception {
        assertEquals("concordion", new ConcordionTestEngine().getId());
    }

    @Test
    void engineLoads() throws Exception {
        // See: META-INF/services/org.junit.platform.engine.TestEngine
        EngineTestKit.engine("concordion");
    }

    @Test
    void engineRunsConcordion() throws Exception {
        RunResultsCache runResultsCache = RunResultsCache.SINGLETON;
        FixtureType demoFixtureType = new FixtureType(DemoTest.class);
        runResultsCache.removeAllFromCache(demoFixtureType);
        FixtureType partialMatchesFixtureType = new FixtureType(PartialMatchesTest.class);
        runResultsCache.removeAllFromCache(partialMatchesFixtureType);
        FixtureType spikeFixtureType = new FixtureType(SpikeTest.class);
        runResultsCache.removeAllFromCache(spikeFixtureType);
        ConcordionTestEngine._clearCacheForTestingOnly();
        
        try {
            EngineExecutionResults executionResults =
                    EngineTestKit.engine("concordion")
                        .selectors(
                            DiscoverySelectors.selectClass(DemoTest.class),
                            DiscoverySelectors.selectClass(PartialMatchesTest.class),
                            DiscoverySelectors.selectClass(SpikeTest.class))
                        .execute();
            executionResults
                    .containerEvents()
                        .assertStatistics(stats -> stats.started(1 + 3));
            executionResults
                    .testEvents()
                        .assertStatistics(stats -> stats.started(3).succeeded(3));

            assertNotNull(runResultsCache.getFromCache(demoFixtureType, null));
            assertNotNull(runResultsCache.getFromCache(partialMatchesFixtureType, null));
            assertNotNull(runResultsCache.getFromCache(spikeFixtureType, null));
        } finally {
            runResultsCache.removeAllFromCache(demoFixtureType);
            runResultsCache.removeAllFromCache(partialMatchesFixtureType);
            runResultsCache.removeAllFromCache(spikeFixtureType);
        }
    }

    @Test
    void engineDiscoversClasses() throws Exception {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .filters(EngineFilter.includeEngines("concordion"))
                .selectors(DiscoverySelectors.selectClass(DemoTest.class))
                .build();
        try (LauncherSession session = LauncherFactory.openSession()) {
            Launcher launcher = session.getLauncher();
            TestPlan testPlan = launcher.discover(request);
            assertTrue(testPlan.containsTests());
        }
    }

    @Test
    void engineDiscoversPackages() throws Exception {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .filters(EngineFilter.includeEngines("concordion"))
                .selectors(DiscoverySelectors.selectPackage(DemoTest.class.getPackage().getName()))
                .build();
        try (LauncherSession session = LauncherFactory.openSession()) {
            Launcher launcher = session.getLauncher();
            TestPlan testPlan = launcher.discover(request);
            assertTrue(testPlan.containsTests());
        }
    }

}

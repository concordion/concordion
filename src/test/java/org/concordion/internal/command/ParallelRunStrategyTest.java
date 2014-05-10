package org.concordion.internal.command;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Runner;
import org.concordion.api.RunnerResult;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.internal.FailFastException;
import org.junit.Test;

public class ParallelRunStrategyTest {
    Resource parentResource = new Resource("/test");
    Map<String, Object> results = new ConcurrentHashMap<String, Object>();
    
    @Test
    public void testCorrectness() {
        int iterations = 100000;
        int threadCount = 100;
        
        Map<String, Object> expectedResults = createResults(iterations);
        ParallelRunStrategy.initialise("" + threadCount);
        
        long startTimeMillis = System.currentTimeMillis();
        long totalSleepMillis = callForEachEntry(expectedResults);
        long actualRunMillis = System.currentTimeMillis() - startTimeMillis;
        
        assertEquals(iterations, results.size());
        for (final Entry<String, Object> expectedResult : expectedResults.entrySet()) {
            String childHref = expectedResult.getKey();
            Object expected = expectedResults.get(childHref);
            Object actual = results.get(childHref);
            assertEquals("For " + childHref, expected, actual);
        }
        
        long minExpectedMillis = totalSleepMillis / threadCount;
        String msg = String.format("Expected run time > %d ms (total %d ms / %d threads) . Actual run time = %d ms", minExpectedMillis, totalSleepMillis, threadCount, actualRunMillis);
        assertTrue(msg, actualRunMillis > minExpectedMillis);
        System.out.println(String.format("%d threads completed %d tasks in %d ms, including total sleep time of %d ms (%d ms per thread)", threadCount, iterations, actualRunMillis, totalSleepMillis, totalSleepMillis / threadCount));
    }  
    
    @Test
    public void testNested() {
        int iterations = 100000;
        int threadCount = 100;
        
        Map<String, Object> expectedResults = createResults(iterations);
        ParallelRunStrategy.initialise("" + threadCount);
        
        long startTimeMillis = System.currentTimeMillis();
        long totalSleepMillis = doChildCall(expectedResults);
        long actualRunMillis = System.currentTimeMillis() - startTimeMillis;
        
        assertEquals(iterations + 1, results.size());
        assertEquals("For " + "myChild", Result.SUCCESS, results.get("myChild"));
        
        for (final Entry<String, Object> expectedResult : expectedResults.entrySet()) {
            String childHref = expectedResult.getKey();
            Object expected = expectedResults.get(childHref);
            Object actual = results.get(childHref);
            assertEquals("For " + childHref, expected, actual);
        }
        
        long minExpectedMillis = totalSleepMillis / threadCount;
        String msg = String.format("Expected run time > %d ms (total %d ms / %d threads) . Actual run time = %d ms", minExpectedMillis, totalSleepMillis, threadCount, actualRunMillis);
        assertTrue(msg, actualRunMillis > minExpectedMillis);
        System.out.println(String.format("%d threads completed %d tasks in %d ms, including total sleep time of %d ms (%d ms per thread)", threadCount, iterations, actualRunMillis, totalSleepMillis, totalSleepMillis / threadCount));
    }  
    
    // TODO additional tests
    // test fail-fast
    // test deeper nesting
    // test roll-up of results
    // test results recorded

    private Map<String, Object> createResults(int iterations) {
        Map<String, Object> expectedResults = new HashMap<String, Object>();
        for (int i = 0; i < iterations; i++) {
            Object result = null;
            switch (i % 5) {
            case 0:
                result = Result.SUCCESS;
                break;

            case 1:
                result = Result.FAILURE;
                break;

            case 2:
                result = Result.IGNORED;
                break;

            case 3:
                result = new IOException();
                break;

            case 4:
                result = new FailFastException("", new Exception());
                break;

            }
            expectedResults.put("child" + i, result);
        }
        return expectedResults;
    }
    
    private final class StubbedResultAnnouncer implements ResultAnnouncer {
        private String child;
        
        public StubbedResultAnnouncer(String child) {
            this.child = child;
        }
        
        @Override
        public void announce(Result result) {
            results.put(child, result);
        }
        
        @Override
        public void announceException(Throwable e) {
            results.put(child, e);
        }
    }
    
    private final class NullResultRecorder implements ResultRecorder {
        public NullResultRecorder() {
        }
        
        @Override
        public void setSpecificationDescription(String description) {
        }
        
        @Override
        public void recordFailFastException(FailFastException exception) {
        }
        
        @Override
        public void record(Result result) {
        }
    }
    
    private long callForEachEntry(Map<String, Object> expectedResults) {
        ParallelRunStrategy parallelRunStrategy = new ParallelRunStrategy();
        parallelRunStrategy.beforeProcessingSpecification(new SpecificationProcessingEvent(parentResource, null));
        long totalSleepMillis = 0;
        for (final Entry<String, Object> expectedResult : expectedResults.entrySet()) {
            String childHref = expectedResult.getKey();
            final Object result = expectedResult.getValue();
            final long sleepMillis = (long) (Math.random() * 10);
            totalSleepMillis += sleepMillis;
                    
            parallelRunStrategy.call(new Runner() {
                @Override
                public RunnerResult execute(Resource resource, String href) throws Exception {
                    Thread.sleep(sleepMillis);
                    if (result instanceof Exception) {
                        throw (Exception) result;
                    }
                    return new RunnerResult((Result)result);
                }
            }, parentResource, childHref , new StubbedResultAnnouncer(childHref), new NullResultRecorder());
        }
        parallelRunStrategy.afterProcessingSpecification(new SpecificationProcessingEvent(parentResource, null));
        return totalSleepMillis;
    }

    private long doChildCall(final Map<String, Object> expectedResults) {
        ParallelRunStrategy parallelRunStrategy = new ParallelRunStrategy();
        parallelRunStrategy.beforeProcessingSpecification(new SpecificationProcessingEvent(parentResource, null));
        String childHref = "myChild";
        final long[] totalSleepMillisWrapper = new long[1]; 
        parallelRunStrategy.call(new Runner() {
            @Override
            public RunnerResult execute(Resource resource, String href) throws Exception {
                totalSleepMillisWrapper[0] = callForEachEntry(expectedResults);
                return new RunnerResult(Result.SUCCESS);
            }
        }, parentResource, childHref, new StubbedResultAnnouncer(childHref), new NullResultRecorder());
        parallelRunStrategy.afterProcessingSpecification(new SpecificationProcessingEvent(parentResource, null));
        return totalSleepMillisWrapper[0];
    }
}


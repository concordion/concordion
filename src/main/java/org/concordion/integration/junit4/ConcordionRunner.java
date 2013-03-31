package org.concordion.integration.junit4;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class ConcordionRunner extends BlockJUnit4ClassRunner {

    private final Description fixtureDescription;
    private final FrameworkMethod fakeMethod;
    private ResultSummary result;

    /*
     * The standard JUnit runner (BlockJUnit4ClassRunner) requires at least 
     * one method marked with the annotation @Test. We don't want this
     * restriction because Concordion tests shouldn't need any methods marked 
     * with @Test. 
     *
     * Unfortunately, if we want to use the rest of the functionality in 
     * BlockJUnit4ClassRunner (processing of @Before, @After, @Test and other
     * annotations), we are either going to have to create a new subclass of 
     * ParentRunner and duplicate a lot of code from BlockJUnitClassRunner  
     * or subclass BlockJUnitClassRunner and (ahem) fine tune it.
     * 
     * I've decided to go with the latter course of action, and:
     * 
     * - Append a FakeFrameworkMethod into the list of test methods.
     * 
     * - Perform different behaviour for that method (i.e. don't call it,
     *   but call our Concordion processing instead).
     *   
     * - Remove the validation that requires at least one @Test method. 
     */
    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        String testDescription = ("[Concordion Specification for '" + fixtureClass.getSimpleName()).replaceAll("Test$", "']"); // Based on suggestion by Danny Guerrier
        fixtureDescription = Description.createTestDescription(fixtureClass, testDescription);
        try {
            fakeMethod = new FakeFrameworkMethod();
        } catch (Exception e) {
            throw new InitializationError("Failed to initialize ConcordionRunner");
        }
    }
    
    static class FakeFrameworkMethod extends FrameworkMethod {

        public FakeFrameworkMethod() {
            super(null);
        }
        
        public String getName() {
            return "[Concordion Specification]";
        }
        
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }
        
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return null;
        }
        
        public int hashCode() {
            return 1;
        }
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> children = new ArrayList<FrameworkMethod>();
        children.addAll(super.getChildren());
        children.add(fakeMethod);
        return children;
    }

    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        if (method == fakeMethod) {
            return specExecStatement(test);
        }
        return super.methodInvoker(method, test);
    }
    
    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method == fakeMethod) {
            return fixtureDescription;
        }
        return super.describeChild(method);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
        if (result != null && result.getIgnoredCount() > 0) {
            notifier.fireTestIgnored(fixtureDescription);
        }
    }

    protected Statement specExecStatement(final Object fixture) {
        return new Statement() {
            public void evaluate() throws Throwable {
                result = new FixtureRunner().run(fixture);
            }
        };
    }

    @Override
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
    }
}

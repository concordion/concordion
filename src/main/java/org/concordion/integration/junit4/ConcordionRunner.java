package org.concordion.integration.junit4;

import org.concordion.Concordion;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.FixtureRunner;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConcordionRunner extends ParentRunner<String> {


    private final Class<?> fixtureClass;
    private ResultSummary result;
    private final Concordion concordion;
    private final ConcordionBuilder concordionBuilder;
    private final List<String> exampleNames;
    private final Object fixture;


    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError, InstantiationException, IllegalAccessException, IOException {
        super(fixtureClass);
        this.fixtureClass = fixtureClass;

        // check all instance methods are setup correctly
        List<Throwable> errors = new ArrayList<Throwable>();
        validateInstanceMethods(errors);
        if (errors.size() > 0) {
            throw new InitializationError(errors);
        }

//        if (fixtureClass.getSimpleName().equals("FileSuffixExtensionsTest")) {
//            System.err.println("");
//        }
        System.err.println(fixtureClass.getSimpleName());

        fixture = fixtureClass.newInstance();
        concordionBuilder = new ConcordionBuilder().withFixture(fixture);
        FixtureExtensionLoader fixtureExtensionLoader = new FixtureExtensionLoader();
        fixtureExtensionLoader.addExtensions(fixture, concordionBuilder);
        concordion = concordionBuilder.build();
        exampleNames = concordion.getExampleNames(fixture);
    }

    @Override
    protected List<String> getChildren() {
        return exampleNames;
    }

    @Override
    protected Description describeChild(String example) {
        return Description.createTestDescription(fixtureClass, example);
    }

    @Override
    protected void runChild(String example, RunNotifier notifier) {
        Description description = describeChild(example);

        try {

            notifier.fireTestStarted(description);

            ResultSummary result = new FixtureRunner().run(fixture, example);

            result.assertIsSatisfied(fixture);

            notifier.fireTestFinished(description);

        } catch (IOException e) {
            notifier.fireTestFailure(new Failure(description, e));
//            notifier.fireTestFinished(description);
        } catch (ConcordionAssertionError e) {
            notifier.fireTestFailure(new Failure(description, e));
//            notifier.fireTestFinished(description);
        }
    }


    protected void validateInstanceMethods(List<Throwable> errors) {

        validatePresenceOfAConstructorThatTakesNoArguments(errors);

    	validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
    	validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
    }

    private void validatePresenceOfAConstructorThatTakesNoArguments(List<Throwable> errors) {
        try {
            fixtureClass.getConstructor(new Class[] {});
        } catch (NoSuchMethodException e) {
            errors.add(e);
        }
    }
}

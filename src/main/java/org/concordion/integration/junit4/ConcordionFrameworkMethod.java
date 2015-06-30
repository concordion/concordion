package org.concordion.integration.junit4;

import org.concordion.Concordion;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;

/**
 * Created by tim on 30/06/15.
 */
class ConcordionFrameworkMethod extends FrameworkMethod {

    private static final Method CONCORDION_METHOD;

    static {
        Method method;
        try {
            method = ConcordionFrameworkMethod.class.getMethod("method", new Class<?>[]{});
        } catch (NoSuchMethodException e) {
            method = null;
            System.err.println("Error when initialisating concordion - could not introspect default method");
        }
        CONCORDION_METHOD = method;
    }

    private final String exampleName;
    private final ConcordionRunnerInterface runner;
    private RunNotifier notifier;


    public ConcordionFrameworkMethod(ConcordionRunnerInterface runner, String exampleName) {
        super(CONCORDION_METHOD);
        this.exampleName = exampleName;
        this.runner = runner;
    }


    public void setNotifier(RunNotifier notifier) {
        this.notifier = notifier;
    }

    public RunNotifier getNotifier() {
        return notifier;
    }


    public void method() {
    }

    public String getExampleName() {
        return exampleName;
    }

    public Object invokeExplosively(final Object target, final Object... params)
            throws Throwable {
        runner.invoke(this);
        return null;
    }

    public interface ConcordionRunnerInterface {
        void invoke(ConcordionFrameworkMethod concordionFrameworkMethod);
    }
}

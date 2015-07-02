package org.concordion.testsuite.examples;

import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionFrameworkMethod;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.awt.*;
import java.util.List;

/**
 * Created by tim on 2/07/15.
 */
@RunWith(ConcordionRunner.class)
public class ExamplesTest {

    private static class ExtendedDefaultConcordionRunner extends DefaultConcordionRunner {
        public Class<?> findTestClass(Resource resource, String href) throws ClassNotFoundException {
            return super.findTestClass(resource, href);
        }
    }

    private static class ExtendedConcordionRunner extends ConcordionRunner {

        public ExtendedConcordionRunner(Class<?> fixtureClass) throws InitializationError {
            super(fixtureClass);
        }

        public void runChild(FrameworkMethod method, RunNotifier notifier) {
            super.runChild(method, notifier);
        }

        @Override
        public List<FrameworkMethod> getChildren() {
            return super.getChildren();
        }
    }

    public int getNumberExamples(String spec) throws Exception {

        ExtendedConcordionRunner runner = getExtendedConcordionRunner(spec);

        List<FrameworkMethod> methods = runner.getChildren();

        return methods.size();
    }

    public String runExample(String spec, String example) throws Exception {
        ExtendedConcordionRunner runner = getExtendedConcordionRunner(spec);

        List<FrameworkMethod> methods = runner.getChildren();

        for (FrameworkMethod frameworkMethod : methods) {
            ConcordionFrameworkMethod concordionFrameworkMethod = (ConcordionFrameworkMethod) frameworkMethod;
            if (concordionFrameworkMethod.getExampleName().equals(example)) {
                runner.runChild(frameworkMethod, new RunNotifier());
                return "Yay!";
            }
        }

        return "nope";
    }



    private ExtendedConcordionRunner getExtendedConcordionRunner(String spec) throws ClassNotFoundException, InitializationError {
        ExtendedDefaultConcordionRunner extendedConcordionRunner = new ExtendedDefaultConcordionRunner();

        final String path = "/" + getClass().getName().replace('.', '/');
        Resource resource = new Resource(path);

        Class<?> fixtureClass = extendedConcordionRunner.findTestClass(resource, spec);

        return new ExtendedConcordionRunner(fixtureClass);
    }

}

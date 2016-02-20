package org.concordion.internal;

import org.concordion.api.Runner;
import org.concordion.api.RunnerFactory;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.concordion.internal.util.Check;

/**
 * Created by tim on 20/02/16.
 */
public class SystemPropertiesRunnerFactory implements RunnerFactory {
    @Override
    public Runner createRunner(String runnerType) throws Exception {
        String concordionRunner = null;

        concordionRunner = System.getProperty("concordion.runner." + runnerType);

        if (concordionRunner == null && "concordion".equals(runnerType)) {
            concordionRunner = DefaultConcordionRunner.class.getName();
        }

        if (concordionRunner == null) {
            try {
                Class.forName(runnerType);
                concordionRunner = runnerType;
            } catch (ClassNotFoundException e1) {
                // OK, we're reporting this in a second.
            }
        }

        Check.notNull(concordionRunner, "The runner '" + runnerType + "' cannot be found. "
                + "Choices: (1) Use 'concordion' as your runner (2) Ensure that the 'concordion.runner." + runnerType
                + "' System property is set to a name of an org.concordion.Runner implementation "
                + "(3) Specify a full class name of an org.concordion.Runner implementation");

        Class<?> clazz = null;

        clazz = Class.forName(concordionRunner);
        Runner runner = (Runner) clazz.newInstance();
        return runner;

    }
}

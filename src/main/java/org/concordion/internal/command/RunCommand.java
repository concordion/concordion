package org.concordion.internal.command;

import java.lang.reflect.Method;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;
import org.concordion.api.Runner;
import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.FailFastException;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class RunCommand extends AbstractCommand {

    private final Announcer<RunListener> listeners = Announcer.to(RunListener.class);

    private final RunStrategy runStrategy;

    public RunCommand(final RunStrategy runStrategy) {
        this.runStrategy = runStrategy;
    }

    public void addRunListener(final RunListener runListener) {
        listeners.addListener(runListener);
    }

    public void removeRunListener(final RunListener runListener) {
        listeners.removeListener(runListener);
    }

    @Override
    public void execute(final CommandCall commandCall, final Evaluator evaluator, final ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'run' is not supported");

        final Element element = commandCall.getElement();

        final String href = element.getAttributeValue("href");

        Check.notNull(href, "The 'href' attribute must be set for an element containing concordion:run");

        final String runnerType = commandCall.getExpression();

        final String expression = element.getAttributeValue("concordion:params");
        if (expression != null)
            evaluator.evaluate(expression);

        final ResultAnnouncer resultAnnouncer = newRunResultAnnouncer(element, expression);

        String concordionRunner = null;

        concordionRunner = System.getProperty("concordion.runner." + runnerType);

        if (concordionRunner == null && "concordion".equals(runnerType)) {
            concordionRunner = DefaultConcordionRunner.class.getName();
        }
        if (concordionRunner == null) {
            try {
                Class.forName(runnerType);
                concordionRunner = runnerType;
            } catch (final ClassNotFoundException e1) {
                // OK, we're reporting this in a second.
            }
        }

        Check.notNull(concordionRunner, "The runner '" + runnerType + "' cannot be found. "
                + "Choices: (1) Use 'concordion' as your runner (2) Ensure that the 'concordion.runner." + runnerType
                + "' System property is set to a name of an org.concordion.Runner implementation "
                + "(3) Specify a full class name of an org.concordion.Runner implementation");
        try {
            final Class<?> clazz = Class.forName(concordionRunner);
            final Runner runner = (Runner) clazz.newInstance();
            for (final Method method : runner.getClass().getMethods()) {
                final String methodName = method.getName();
                if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterTypes().length == 1) {
                    final String variableName = methodName.substring(3, 4).toLowerCase() + method.getName().substring(4);
                    Object variableValue = evaluator.evaluate(variableName);
                    if (variableValue == null) {
                        try {
                            variableValue = evaluator.getVariable(variableName);
                        } catch (final Exception e) {
                        }
                    }
                    if (variableValue != null) {
                        try {
                            method.invoke(runner, variableValue);
                        } catch (final Exception e) {
                        }
                    }
                }
            }

            runStrategy.call(runner, commandCall.getResource(), href, resultAnnouncer, resultRecorder);

        } catch (final FailFastException e) {
            throw e; // propagate FailFastExceptions
        } catch (final ConcordionAssertionError e) {
        	resultAnnouncer.announceException(e);
        	resultRecorder.record(e.getResultSummary());
        } catch (final Exception e) {
            resultAnnouncer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }

    }

    private ResultAnnouncer newRunResultAnnouncer(final Element element, final String expression) {
        return new ResultAnnouncer() {
            @Override
            public void announce(final ResultSummary result) {
            	if (result.getFailureCount() + result.getExceptionCount() > 0) {
                    listeners.announce().failureReported(new RunFailureEvent(element));
            	} else if (result.getIgnoredCount() > 0) {
                    listeners.announce().ignoredReported(new RunIgnoreEvent(element));
            	} else {
                    listeners.announce().successReported(new RunSuccessEvent(element));
              	}
            }

            @Override
            public void announceException(final Throwable throwable) {
                listeners.announce().throwableCaught(new ThrowableCaughtEvent(throwable, element, expression));
            }
        };
    }
}

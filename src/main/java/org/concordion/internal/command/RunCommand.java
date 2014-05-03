package org.concordion.internal.command;

import java.lang.reflect.Method;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Runner;
import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.internal.FailFastException;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class RunCommand extends AbstractCommand {

    private Announcer<RunListener> listeners = Announcer.to(RunListener.class);
    
    private final RunStrategy runStrategy;

    public RunCommand(RunStrategy runStrategy) {
        this.runStrategy = runStrategy;
    }
    
    public void addRunListener(RunListener runListener) {
        listeners.addListener(runListener);
    }

    public void removeRunListener(RunListener runListener) {
        listeners.removeListener(runListener);
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'run' is not supported");

        Element element = commandCall.getElement();

        final String href = element.getAttributeValue("href");

        Check.notNull(href, "The 'href' attribute must be set for an element containing concordion:run");

        String runnerType = commandCall.getExpression();

        String expression = element.getAttributeValue("concordion:params");
        if (expression != null)
            evaluator.evaluate(expression);

        ResultAnnouncer resultAnnouncer = newRunResultAnnouncer(element, expression);
        
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
        try {
            Class<?> clazz = Class.forName(concordionRunner);
            final Runner runner = (Runner) clazz.newInstance();
            for (Method method : runner.getClass().getMethods()) {
                String methodName = method.getName();
                if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterTypes().length == 1) {
                    String variableName = methodName.substring(3, 4).toLowerCase() + method.getName().substring(4);
                    Object variableValue = evaluator.evaluate(variableName);
                    if (variableValue == null) {
                        try {
                            variableValue = evaluator.getVariable(variableName);
                        } catch (Exception e) {
                        }
                    }
                    if (variableValue != null) {
                        try {
                            method.invoke(runner, variableValue);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            
            runStrategy.call(runner, commandCall.getResource(), href, resultAnnouncer, resultRecorder);
            
        } catch (FailFastException e) { 
            throw e; // propagate FailFastExceptions
        } catch (Exception e) {
            resultAnnouncer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }

    }

    private ResultAnnouncer newRunResultAnnouncer(final Element element, final String expression) {
        return new ResultAnnouncer() {
            @Override
            public void announce(Result result) {
                switch (result) {
                case SUCCESS:
                    listeners.announce().successReported(new RunSuccessEvent(element));
                    break;
                
                case FAILURE:
                case EXCEPTION: // If the test runner returns a RunnerResult containing an exception this is reported as a failure, not as an exception.
                    listeners.announce().failureReported(new RunFailureEvent(element));
                    break;
                
                case IGNORED:
                    listeners.announce().ignoredReported(new RunIgnoreEvent(element));
                    break;
                }
            }
                
            @Override
            public void announceException(Throwable throwable) {
                listeners.announce().throwableCaught(new ThrowableCaughtEvent(throwable, element, expression));
            }
        };
    }
}

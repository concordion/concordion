package org.concordion.internal.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.listener.*;
import org.concordion.internal.ConcordionAssertionError;
import org.concordion.internal.FailFastException;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.concordion.internal.util.Check;

public class RunCommand extends AbstractCommand {

    private List<RunListener> listeners = new ArrayList<RunListener>();
    private RunStrategy runStrategy = new SequentialRunStrategy();

    public void addRunListener(RunListener runListener) {
        listeners.add(runListener);
    }

    public void removeRunListener(RunListener runListener) {
        listeners.remove(runListener);
    }
    
    public void setRunStrategy(RunStrategy runStrategy) {
        this.runStrategy = runStrategy;
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'run' is not supported");

        Element element = commandCall.getElement();

        String href = element.getAttributeValue("href");

        Check.notNull(href, "The 'href' attribute must be set for an element containing concordion:run");

        String runnerType = commandCall.getExpression();

        String expression = commandCall.getParameter("params");
        if (expression != null)
            evaluator.evaluate(expression);

        ResultAnnouncer resultAnnouncer = newRunResultAnnouncer(commandCall.getResource(), element, expression);

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
            Runner runner = (Runner) clazz.newInstance();
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
            updateHrefSuffix(element, href);

        } catch (FailFastException e) {
            throw e; // propagate FailFastExceptions
        } catch (ConcordionAssertionError e) {
        	resultAnnouncer.announceException(e);
        	resultRecorder.record(e.getResultSummary());
        } catch (Exception e) {
            resultAnnouncer.announceException(e);
            resultRecorder.record(Result.FAILURE);
        }

    }

    /* For non-html format specs, update the href to reference the html output */
    private void updateHrefSuffix(Element element, String href) {
        if (!(href.endsWith(".html"))) {
            String modifiedHref = href.substring(0, href.lastIndexOf(".")) + ".html";
            element.removeAttribute("href");
            element.addAttribute("href", modifiedHref);
        }
    }

    private ResultAnnouncer newRunResultAnnouncer(final Resource resource, final Element element, final String expression) {
        return new ResultAnnouncer() {
            public void announce(ResultSummary result) {
                synchronized(resource) {
                    if (result.getFailureCount() + result.getExceptionCount() > 0) {
                    	announceFailure(element, result);
                    } else if (result.getIgnoredCount() > 0) {
                    	announceIgnored(element, result);
                    } else {
                    	announceSuccess(element, result);
                    }
                }
            }

			public void announceException(Throwable throwable) {
                synchronized (resource) {
                	for (RunListener listener : listeners) {
                		listener.throwableCaught(new ThrowableCaughtEvent(throwable, element, expression));
					}
                }
            }
			
			private void announceFailure(Element element, ResultSummary result) {
				for (RunListener listener : listeners) {
					listener.failureReported(new RunFailureEvent(element, result));
				}
			}
			
			private void announceIgnored(Element element, ResultSummary result) {
				for (RunListener listener : listeners) {
					listener.ignoredReported(new RunIgnoreEvent(element, result));
				}
			}
			
			private void announceSuccess(Element element, ResultSummary result) {
				for (RunListener listener : listeners) {
					listener.successReported(new RunSuccessEvent(element, result));
				}
			}
        };
    }
}

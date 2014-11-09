package org.concordion.internal.command;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Resource;
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
    /**
     * Cache of results that have already been run so we don't
     * rerun a spec twice.  Must be static because each call
     * to {@code concordion:run} creates a new instance.
     */
    private static Map<Resource, Result> RESULT_CACHE = new ConcurrentHashMap<Resource, Result>();
    
    /**
     * Set of specs that are in the middle of being executed (and therefore haven't been added to the {@link #RESULT_CACHE}
     * yet.  We won't re-execute anything that is still running to avoid infinite loops where a child spec references its parent.
     */
    private static Set<Resource> PENDING_RESULTS = Collections.newSetFromMap(new ConcurrentHashMap<Resource, Boolean>());
    
    
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

        String href = element.getAttributeValue("href");

        Check.notNull(href, "The 'href' attribute must be set for an element containing concordion:run");

        String runnerType = commandCall.getExpression();

        String expression = element.getAttributeValue("concordion:params");
        if (expression != null)
            evaluator.evaluate(expression);

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
        
        Resource hrefResource = commandCall.getResource().getParent().getRelativeResource(href);
     
        Result cachedResult = RESULT_CACHE.get(hrefResource);
        
       
        if(cachedResult !=null){
        	//already ran this spec
        	//notify listeners so we render the page accordingly
        	if (cachedResult == Result.SUCCESS) {
                announceSuccess(element);
            } else if (cachedResult == Result.IGNORED) {
                announceIgnored(element);
            } else {
                announceFailure(element);
            }
        	
        	//but don't update the resultRecorder!
        	return;
        }
        //check if this is a pending result
        if(PENDING_RESULTS.contains(hrefResource)){
        	//do nothing
        	return;
        }
        
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
            try {
            	PENDING_RESULTS.add(hrefResource);
                Result result = runner.execute(commandCall.getResource(), href).getResult();

                if (result == Result.SUCCESS) {
                    announceSuccess(element);
                } else if (result == Result.IGNORED) {
                    announceIgnored(element);
                } else {
                    announceFailure(element);
                }
                resultRecorder.record(result);
                
                RESULT_CACHE.put(hrefResource, result);
                
            } catch (FailFastException e) { 
                throw e;
            } catch (Throwable e) {
                announceFailure(e, element, runnerType);
                resultRecorder.record(Result.FAILURE);
                RESULT_CACHE.put(hrefResource, Result.FAILURE);
            }finally{
            	PENDING_RESULTS.remove(hrefResource);
            }
        } catch (FailFastException e) { 
            throw e; // propagate FailFastExceptions
        } catch (Exception e) {
            announceFailure(e, element, runnerType);
            resultRecorder.record(Result.FAILURE);
            RESULT_CACHE.put(hrefResource, Result.FAILURE);
        }

    }

    private void announceIgnored(Element element) {
        listeners.announce().ignoredReported(new RunIgnoreEvent(element));
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunFailureEvent(element));
    }

    private void announceFailure(Throwable throwable, Element element, String expression) {
        listeners.announce().throwableCaught(new ThrowableCaughtEvent(throwable, element, expression));
    }
}

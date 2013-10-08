package org.concordion.internal.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ParallelRunner;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.RunnerResult;
import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.internal.FailFastException;
import org.concordion.internal.runner.DefaultConcordionParallelRunner;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class ParallelRunCommand extends AbstractCommand {

    private Announcer<RunListener> listeners = Announcer.to(RunListener.class);
    private Map<Resource, Map<Future<RunnerResult>, ResultTarget>> resourceTaskMap = new ConcurrentHashMap<Resource, Map<Future<RunnerResult>,ResultTarget>>();
    public static ThreadLocalExecutor threadLocalExecutor = new ThreadLocalExecutor();
    private static ThreadLocalExecutorCompletionService threadLocalExecutorCompletionService = new ThreadLocalExecutorCompletionService();
    
    public static class ThreadLocalExecutor extends InheritableThreadLocal<ThreadPoolExecutor> {
        @Override
        protected ThreadPoolExecutor initialValue() {
            return (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        }
    }
    
    private static class ThreadLocalExecutorCompletionService extends ThreadLocal<ExecutorCompletionService<RunnerResult>> {
        @Override
        protected ExecutorCompletionService<RunnerResult> initialValue() {
            return new ExecutorCompletionService<RunnerResult>(threadLocalExecutor.get());
        }
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

        String href = element.getAttributeValue("href");

        Check.notNull(href, "The 'href' attribute must be set for an element containing concordion:run");

        String runnerType = commandCall.getExpression();

        String expression = element.getAttributeValue("concordion:params");
        if (expression != null)
            evaluator.evaluate(expression);

        String concordionRunner = null;

        concordionRunner = System.getProperty("concordion.runner." + runnerType);

        if (concordionRunner == null && "concordion".equals(runnerType)) {
            
//TODO allow parallel keyword that defaults to DefaultConcordionParallelRunner             
//            concordionRunner = DefaultConcordionRunner.class.getName();
            concordionRunner = DefaultConcordionParallelRunner.class.getName();
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

            //TODO could check for runner or parallel runner?
            ParallelRunner runner = (ParallelRunner) clazz.newInstance();
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
                Resource resource = commandCall.getResource();
                Callable<RunnerResult> task = runner.createRunnerTask(resource, href);
                Future<RunnerResult> future = submitTask(task);
                Map<Future<RunnerResult>, ResultTarget> taskMap = getTaskMapForResource(resource);
                taskMap.put(future, new ResultTarget(element, resultRecorder));
                
            } catch (Throwable e) {
                announceFailure(e, element, runnerType);
                resultRecorder.record(Result.FAILURE);
            }
        } catch (FailFastException e) { 
            throw e; // propagate FailFastExceptions
        } catch (Exception e) {
            announceFailure(e, element, runnerType);
            resultRecorder.record(Result.FAILURE);
        }

    }

    private Future<RunnerResult> submitTask(Callable<RunnerResult> task) {
        ExecutorCompletionService<RunnerResult> completionService = threadLocalExecutorCompletionService.get();
        Future<RunnerResult> future = completionService.submit(task);
        return future;
    }
    
    private Map<Future<RunnerResult>, ResultTarget> getTaskMapForResource(Resource resource) {
        Map<Future<RunnerResult>, ResultTarget> taskMap = resourceTaskMap.get(resource);
        if (taskMap == null) {
            taskMap = new HashMap<Future<RunnerResult>, ResultTarget>();
            resourceTaskMap.put(resource, taskMap);
        }
        return taskMap;
    }

    public void waitForCompletion(Resource resource) {
        Map<Future<RunnerResult>, ResultTarget> taskMap = resourceTaskMap.get(resource);
//        System.out.println(resource + " " + taskMap);
        if (taskMap != null) {
            while (taskMap.size() > 0) {
                try {
                    ExecutorCompletionService<RunnerResult> completionService = threadLocalExecutorCompletionService.get();
                    Future<RunnerResult> task = completionService.take();
                    Result result = task.get().getResult(); 
                    ResultTarget resultTarget = taskMap.remove(task);
                
                    onCompletion(result, resultTarget.getResultRecorder(), resultTarget.getElement());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }        
//        System.out.println(resource + " finished ");

    }

    private void onCompletion(Result result, ResultRecorder resultRecorder, Element element) {
        if (result == Result.SUCCESS) {
            announceSuccess(element);
        } else if (result == Result.IGNORED) {
            announceIgnored(element);
        } else {
            announceFailure(element);
        }
        resultRecorder.record(result);
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
    
    private static class ResultTarget {

        private final Element element;
        private final ResultRecorder resultRecorder;

        public ResultTarget(Element element, ResultRecorder resultRecorder) {
            this.element = element;
            this.resultRecorder = resultRecorder;
        }

        public Element getElement() {
            return element;
        }

        public ResultRecorder getResultRecorder() {
            return resultRecorder;
        }
    }
}

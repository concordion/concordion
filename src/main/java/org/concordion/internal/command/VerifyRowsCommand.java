package org.concordion.internal.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.command.strategies.DefaultMatchStrategy;
import org.concordion.internal.command.strategies.RowsMatchStrategy;
import org.concordion.internal.util.Check;

public class VerifyRowsCommand extends AbstractCommand {

    private List<VerifyRowsListener> listeners = new ArrayList<VerifyRowsListener>();

    public void addVerifyRowsListener(VerifyRowsListener listener) {
        listeners.add(listener);
    }

    public void removeVerifyRowsListener(VerifyRowsListener listener) {
        listeners.remove(listener);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Pattern pattern = Pattern.compile("(#.+?) *: *(.+)");
        Matcher matcher = pattern.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRows\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        Check.isTrue(!(obj instanceof HashSet) || (obj instanceof LinkedHashSet), obj.getClass().getCanonicalName() + " does not have a predictable iteration order");
        Iterable<Object> iterable = (Iterable<Object>) obj;

        newStrategyInstance(detectStrategyClass(commandCall), commandCall, evaluator, resultRecorder, loopVariableName, iterable).verify();
    }

    private static final String DEFAULT_STRATEGIES_PACKAGE = DefaultMatchStrategy.class.getPackage().getName() + '.';
    private static final String DEFAULT_STRATEGIES_SUFFIX = "Strategy";

    private RowsMatchStrategy newStrategyInstance(Class<? extends RowsMatchStrategy> strategyClass,
                CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, String loopVariableName, Iterable<Object> iterable) {
        Constructor<? extends RowsMatchStrategy> constructor;
        try {
            constructor = strategyClass.getConstructor(CommandCall.class, Evaluator.class, ResultRecorder.class, List.class, String.class, Iterable.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(strategyClass.getName() + " must declare constructor with arguments: " +
                    "CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,\n" +
                    "List<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows");
        }
        try {
            return constructor.newInstance(commandCall, evaluator, resultRecorder, listeners, loopVariableName, iterable);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<? extends RowsMatchStrategy> detectStrategyClass(CommandCall commandCall) {
        String strategy = commandCall.getParameter("matchStrategy", "match-strategy");
        if (strategy == null || DefaultMatchStrategy.DEFAULT_STRATEGY_NAME.equalsIgnoreCase(strategy)) {
            return DefaultMatchStrategy.class;
        }
        Class<? extends RowsMatchStrategy> strategyClass =
                findFirstExistingClass(DEFAULT_STRATEGIES_PACKAGE + strategy + DEFAULT_STRATEGIES_SUFFIX, strategy);
        if (strategyClass == null) {
            throw new IllegalArgumentException("MatchStrategy '" + strategy + "' is not found");
        }
        return strategyClass;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends RowsMatchStrategy> findFirstExistingClass(String... names) {
        for (String name : names) {
            try {
                Class<?> aClass = Class.forName(name);
                if (RowsMatchStrategy.class.isAssignableFrom(aClass)) {
                    return (Class<? extends RowsMatchStrategy>) aClass;
                }
            } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }
}

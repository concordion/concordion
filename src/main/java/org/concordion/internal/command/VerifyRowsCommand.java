package org.concordion.internal.command;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.*;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.command.strategies.Default;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class VerifyRowsCommand extends AbstractCommand {

    private Announcer<VerifyRowsListener> listeners = Announcer.to(VerifyRowsListener.class);

    public void addVerifyRowsListener(VerifyRowsListener listener) {
        listeners.addListener(listener);
    }

    public void removeVerifyRowsListener(VerifyRowsListener listener) {
        listeners.removeListener(listener);
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

        VerifyRowsStrategy verifyRowsStrategy;

        try {
            verifyRowsStrategy = detectStrategyClass(commandCall)
                    .getConstructor(CommandCall.class, Evaluator.class, ResultRecorder.class, Announcer.class, String.class, Iterable.class)
                    .newInstance(commandCall, evaluator, resultRecorder, listeners, loopVariableName, iterable);
        } catch (Exception e) {
            throw new RuntimeException("Verification strategy must declare constructor with arguments: " +
                    "CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,\n" +
                    "Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows");
        }

        verifyRowsStrategy.verify();
    }

    private static final String DEFAULT_STRATEGIES_PACKAGE = "org.concordion.internal.command.strategies.";

    private Class<? extends VerifyRowsStrategy> detectStrategyClass(CommandCall commandCall) {
        String strategy = commandCall.getElement().getConcordionAttributeValue("verificationStrategy", "verification-strategy");
        if (strategy == null) {
            return Default.class;
        }
        return findFirstExistingClassOrDefault(DEFAULT_STRATEGIES_PACKAGE + strategy, strategy);
    }

    private Class<? extends VerifyRowsStrategy> findFirstExistingClassOrDefault(String... names) {
        for (String name : names) {
            try {
                Class<?> aClass = Class.forName(name);
                if (VerifyRowsStrategy.class.isAssignableFrom(aClass)) {
                    return (Class<? extends VerifyRowsStrategy>) aClass;
                }
            } catch (ClassNotFoundException ignored) {}
        }
        return Default.class;
    }
}

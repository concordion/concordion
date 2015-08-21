package org.concordion.internal.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.Row;
import org.concordion.internal.TableSupport;
import org.concordion.internal.command.strategies.DefaultStrategy;
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
        
        Class<? extends VerificationStrategy> clazz = detectStrategyClass(commandCall);
        try {
            clazz.getConstructor(
                    CommandCall.class,
                    Evaluator.class,
                    ResultRecorder.class,
                    Announcer.class,
                    String.class,
                    Iterable.class
            ).newInstance(commandCall, evaluator, resultRecorder, listeners, loopVariableName, iterable).verify();
        } catch (Exception e) {
            throw new RuntimeException("Verification strategy must declare constructor with arguments: " +
                    "CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,\n" +
                    "Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows");
        }
    }

    private static final String DEFAULT_STRATEGIES_PACKAGE = "org.concordion.internal.command.strategies.";

    private Class<? extends VerificationStrategy> detectStrategyClass(CommandCall commandCall) {
        String strategy = findAnyAttributeValue(commandCall, "verificationStrategy", "verification-strategy");
        if (strategy == null) {
            return DefaultStrategy.class;
        }
        Class<? extends VerificationStrategy> clazz = findClass(DEFAULT_STRATEGIES_PACKAGE + strategy);
        if (clazz != null) {
            return clazz;
        }
        clazz = findClass(strategy);
        if (clazz != null) {
            return clazz;
        }
        return DefaultStrategy.class;
    }

    private String findAnyAttributeValue(CommandCall commandCall, String... attributeNames) {
        for (String attributeName : attributeNames) {
            String value = commandCall.getElement().getAttributeValue(attributeName, ConcordionBuilder.NAMESPACE_CONCORDION_2007);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Class<? extends VerificationStrategy> findClass(String name) {
        try {
            Class<?> aClass = Class.forName(name);
            if (VerificationStrategy.class.isAssignableFrom(aClass)) {
                return (Class<? extends VerificationStrategy>) aClass;
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static abstract class VerificationStrategy {

        protected final CommandCall commandCall;
        protected final Evaluator evaluator;
        protected final ResultRecorder resultRecorder;
        protected final Announcer<VerifyRowsListener> listeners;
        protected final String loopVariableName;
        protected final TableSupport tableSupport;
        protected final Row[] expectedRows;
        protected final List<Object> actualRows;

        public VerificationStrategy(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder,
                                    Announcer<VerifyRowsListener> listeners, String loopVariableName, Iterable<Object> actualRows) {
            this.commandCall = commandCall;
            this.evaluator = evaluator;
            this.resultRecorder = resultRecorder;
            this.listeners = listeners;
            this.loopVariableName = loopVariableName;
            this.tableSupport = new TableSupport(commandCall);
            this.expectedRows = tableSupport.getDetailRows();
            this.actualRows = copy(actualRows);
        }

        public abstract void verify();

        protected void announceExpressionEvaluated(Element element) {
            listeners.announce().expressionEvaluated(new ExpressionEvaluatedEvent(element));
        }

        protected void announceMissingRow(Element element) {
            listeners.announce().missingRow(new MissingRowEvent(element));
        }

        protected void announceSurplusRow(Element element) {
            listeners.announce().surplusRow(new SurplusRowEvent(element));
        }

        protected List<Object> copy(Iterable<Object> iterable) {
            List<Object> copy = new ArrayList<Object>();
            for (Object o : iterable) {
                copy.add(o);
            }
            return copy;
        }
    }
}

package org.concordion.internal.util;

public class Check {

    public static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new RuntimeException(String.format(message, args));
        }
    }

    public static void isFalse(boolean expression, String message, Object... args) {
        isTrue(!expression, message, args);
    }

    public static void notNull(Object object, String message, Object... args) {
        isTrue(object != null, message, args);
    }

    public static void notEmpty(String s, String message, Object... args) {
        isTrue(s != null && s.length() > 0, message, args);
    }

}

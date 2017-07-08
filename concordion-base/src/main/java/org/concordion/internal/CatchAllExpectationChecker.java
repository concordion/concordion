package org.concordion.internal;
public class CatchAllExpectationChecker implements ExpectationChecker {

    public boolean isAcceptable(Object actual, String expected) {
        return normalize(actual).equals(normalize(expected));
    }
    
    public static String normalize(final Object object) {
        String s = convertObjectToString(object);
        s = processLineContinuations(s);
        s = replaceMultipleWhitespaceWithOneSpace(s);
        return s.trim();
    }

    private static String convertObjectToString(Object object) {
        if (object == null) {
            return "(null)";
        }
        return "" + object;
    }

    private static String replaceMultipleWhitespaceWithOneSpace(String s) {
        return s.replaceAll("[\\s\\n\\r]+", " ");
    }

    private static String processLineContinuations(String s) {
        return s.replaceAll(" _[\n\r+]", "");
    }
}

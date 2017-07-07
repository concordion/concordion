package org.concordion.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MultiPattern {

    private final List<Pattern> patterns;

    private MultiPattern(List<Pattern> patterns) {
        this.patterns = Collections.unmodifiableList(patterns);
    }

    public boolean matches(String input) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(input).matches()) {
                return true;
            }
        }
        return false;
    }

    public static MultiPattern fromRegularExpressions(String... regularExpressions) {
        List<Pattern> patterns = new ArrayList<Pattern>(regularExpressions.length);
        for (String regularExpression : regularExpressions) {
            patterns.add(Pattern.compile(regularExpression));
        }
        return new MultiPattern(patterns);
    }
}

package spec.concordion.command.assertEquals.whitespace;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.internal.CatchAllExpectationChecker;
import test.concordion.TestRig;

public class WhitespaceTest extends ConcordionTestCase {

    public String whichSnippetsSucceed(String snippet1, String snippet2, String evaluationResult) throws Exception {
        return which(succeeds(snippet1, evaluationResult), succeeds(snippet2, evaluationResult));
    }

    public String whichSnippetsFail(String snippet1, String snippet2, String evaluationResult) throws Exception {
        return which(fails(snippet1, evaluationResult), fails(snippet2, evaluationResult));
    }

    private static String which(boolean b1, boolean b2) {
        if (b1 && b2) {
            return "both";
        } else if (b1) {
            return "the first of";
        } else if (b2) {
            return "the second of";
        }
        return "neither";
    }

    private boolean fails(String snippet, String evaluationResult) throws Exception { 
        return !succeeds(snippet, evaluationResult);
    }
    
    private boolean succeeds(String snippet, String evaluationResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(snippet)
            .isSuccess();
    }
    
    public String normalize(String s) {
        // Bit naughty calling internal method normalize() directly 
        return replaceRealWhitespaceCharactersWithNames(
                CatchAllExpectationChecker.normalize(replaceNamedWhitespaceWithRealWhitespaceCharacters(s)));
    }
    
    private static String replaceNamedWhitespaceWithRealWhitespaceCharacters(String s) {
        return s.replaceAll("\\[SPACE\\]", " ")
            .replaceAll("\\[TAB\\]", "\t")
            .replaceAll("\\[LF\\]", "\n")
            .replaceAll("\\[CR\\]", "\r");
    }

    private static String replaceRealWhitespaceCharactersWithNames(String s) {
        return s.replaceAll(" ", "[SPACE]");
    }
    
}

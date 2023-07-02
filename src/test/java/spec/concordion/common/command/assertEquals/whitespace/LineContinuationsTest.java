package spec.concordion.common.command.assertEquals.whitespace;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class LineContinuationsTest {
    
    private List<String> snippets = new ArrayList<String>();
    
    public void addSnippet(String snippet) {
        snippets.add(snippet);
    }
    
    public Result processSnippets(String evaluationResult) {
        Result result = new Result();

        int i = 1;
        for (String snippet : snippets) {
            if (new TestRig()
                    .withStubbedEvaluationResult(evaluationResult)
                    .processFragment(snippet)
                    .hasFailures()) {
                result.failures += "(" + i + "), ";
            } else {
                result.successes += "(" + i + "), ";
            }
            i++;
        }
        result.failures = result.failures.replaceAll(", $", "");
        result.successes = result.successes.replaceAll(", $", "");
        
        return result;
    }
    
    @RunWith(ConcordionRunner.class)
    public class Result {
        public String successes = "";
        public String failures = "";
    }
}

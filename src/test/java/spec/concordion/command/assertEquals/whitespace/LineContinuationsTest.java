package spec.concordion.command.assertEquals.whitespace;

import java.util.ArrayList;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
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

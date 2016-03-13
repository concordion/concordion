package spec.concordion.specificationType.markdown;

import org.concordion.api.ConcordionResources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@ConcordionResources({"img/*","Markdown.css"})
public class MarkdownFixture {
    
    public void parseNode(String text, int level) {
        System.out.println(text + " : " + level);
    }
    
    public String greetingFor(String firstName) {
        return "Hello Bob!";
    }
}

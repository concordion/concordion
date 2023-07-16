package spec.concordion.specificationType.markdown;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.ConcordionResources;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@ConcordionResources({"img/*","Markdown.css"})
public class MarkdownFixture {
}

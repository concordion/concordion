package spec.concordion.common.extension;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@Extensions(SxhtmlExtension.class)
public class FileSuffixExtensionsTest {

    public boolean hasBeenProcessed() {
        return true;
    }
}

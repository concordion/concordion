package spec.concordion.common.extension;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
@Extensions(XhtmlExtension.class)
public class FileSuffixExtensionsTest {

    public boolean hasBeenProcessed() {
        return true;
    }
}

package spec.concordion.command.verifyRows.strategies;

import static org.concordion.api.MultiValueResult.multiValueResult;

import org.concordion.api.MultiValueResult;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.Extension;
import org.concordion.ext.EmbedExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class DefaultMatchStrategyTest extends BaseMatchStrategyTest {
    
    private static final String ROWS_PLACEHOLDER = "\n   [ROWS]\n";
    @Extension
    public ConcordionExtension extension =
        new EmbedExtension().withNamespace("concordion", "http://www.concordion.org/2007/concordion");

    public MultiValueResult processRows(String template, String expectedRows, String actualData) throws Exception {
        String expectedTable = wrapRows(template, expectedRows);
        String resultTable = processFragment(expectedTable, actualData);
        String resultRows = unwrapRows(template, resultTable);
        return multiValueResult()
                .with("expectedTableCommented", "<!--" + expectedTable + "-->")
                .with("resultTableCommented", "<!--" + resultTable + "-->")
                .with("resultRows", resultRows);
    }

    private String wrapRows(String template, String fragment) {
        return template.replace(ROWS_PLACEHOLDER, fragment);
    }

    private String unwrapRows(String template, String actualTable) {
        String tableStart = template.substring(0, template.indexOf(ROWS_PLACEHOLDER));
        String tableEnd = template.substring(template.indexOf(ROWS_PLACEHOLDER) + ROWS_PLACEHOLDER.length());
        String rows = actualTable.replace(tableStart, "").replace(tableEnd, "");
        return rows;
    }
}

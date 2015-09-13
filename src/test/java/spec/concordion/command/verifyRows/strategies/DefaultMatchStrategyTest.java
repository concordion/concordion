package spec.concordion.command.verifyRows.strategies;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class DefaultMatchStrategyTest extends BaseMatchStrategyTest {
    
    private static final String ROWS_PLACEHOLDER = "\n   [ROWS]\n";

    public String processRows(String template, String expectedRows, String actualData) throws Exception {
        String expectedTable = wrapRows(template, expectedRows);
        String resulTable = processFragment(expectedTable, actualData);
        String resultRows = unwrapRows(template, resulTable);
        return resultRows;
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

package spec.concordion.command.verifyRows.strategies;

import nu.xom.Document;
import org.concordion.api.MultiValueResult;
import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.List;

import static org.concordion.api.MultiValueResult.multiValueResult;

public class BaseMatchStrategyTest {

    public List<MultiValueResult> users;

    private static final String ROWS_PLACEHOLDER = "\n   [ROWS]\n";

    public String processRows(String template, String expectedRows, String actualData) throws Exception {
        return unwrapRows(template,
                processFragment(
                        wrapRows(template, expectedRows),
                        actualData
                )
        );
    }

    private String wrapRows(String template, String fragment) {
        return template.replace(ROWS_PLACEHOLDER, fragment);
    }

    private String unwrapRows(String template, String actualTable) {
        String tableStart = template.substring(0, template.indexOf(ROWS_PLACEHOLDER));
        String tableEnd = template.substring(template.indexOf(ROWS_PLACEHOLDER) + ROWS_PLACEHOLDER.length());
        return actualTable.replace(tableStart, "").replace(tableEnd, "");
    }

    public String processFragment(String fragment, String actualData) throws Exception {
        users = parse(actualData);

        Document document = new TestRig()
                .withFixture(this)
                .processFragment(fragment)
                .getXOMDocument();

        return document.getRootElement().query("//table").get(0).toXML();
    }

    private List<MultiValueResult> parse(String actual) {
        List<MultiValueResult> result = new ArrayList<MultiValueResult>();
        for (String single : actual.replace('\n', ' ').trim().split(";")) {
            result.add(parseSingle(single));
        }
        return result;
    }

    private MultiValueResult parseSingle(String single) {
        String[] words = single.substring(1, single.length() - 1).split(",");
        return multiValueResult()
                .with("firstName", words[0])
                .with("lastName", words[1])
                .with("age", words[2]);
    }
}

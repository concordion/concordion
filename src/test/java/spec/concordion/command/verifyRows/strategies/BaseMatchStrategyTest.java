package spec.concordion.command.verifyRows.strategies;

import static org.concordion.api.MultiValueResult.multiValueResult;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;

import org.concordion.api.MultiValueResult;
import org.concordion.api.Resource;
import org.concordion.api.extension.Extensions;
import org.concordion.ext.EmbedExtension;

import test.concordion.TestRig;
import extension.SpecificationToggle.SpecificationToggleExtension;

@Extensions({EmbedExtension.class, SpecificationToggleExtension.class})
public class BaseMatchStrategyTest {

    public List<MultiValueResult> users;

    private static final String ROWS_PLACEHOLDER = "\n   [ROWS]\n";

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
        return actualTable.replace(tableStart, "").replace(tableEnd, "");
    }

    public String processFragment(String fragment, String actualData) throws Exception {
        users = parse(actualData);
        Document document = new TestRig()
                .withFixture(this)
                .withResource(new Resource("/toggle_html.js"), "")
                .withResource(new Resource("/toggle_html.css"), "")
                .processFragment(fragment)
                .getXOMDocument();

        String xml = document.getRootElement().query("//table").get(0).toXML();
        return xml;
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

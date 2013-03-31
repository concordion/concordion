package spec.concordion.command.verifyRows;

import java.util.ArrayList;
import java.util.Collection;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.internal.util.Check;

import test.concordion.TestRig;

public class VerifyRowsTest extends ConcordionTestCase {
    public Collection<String> usernames;

    public String processFragment(String fragment, String csv) throws Exception {
        usernames = csvToCollection(csv);
        Document document = new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getXOMDocument();

        String result = "";
        Element table = (Element) document.getRootElement().query("//table").get(0);
        Nodes rows = table.query(".//tr");
        for (int i = 1; i < rows.size(); i++) {
            if (!result.equals("")) {
                result += ", ";
            }
            result += categorize((Element)rows.get(i));
        }

        return result;
    }

    private String categorize(Element row) {
        String cssClass = row.getAttributeValue("class");
        if (cssClass == null) {
            Element cell = (Element) row.query("td").get(0);
            cssClass = cell.getAttributeValue("class");
        }
        Check.notNull(cssClass, "cssClass is null");
        return cssClass.toUpperCase();
    }

    private static Collection<String> csvToCollection(String csv) {
        Collection<String> c = new ArrayList<String>();
        for (String s : csv.split(", ?")) {
            c.add(s);
        }
        return c;
    }
}

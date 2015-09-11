package spec.concordion.command.verifyRows;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.util.Check;
import org.junit.runner.RunWith;
import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(ConcordionRunner.class)
public class VerifyRowsUnorderedTest {
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
        String value = row.getValue();
        if (cssClass == null || value == null) {
            Element cell = (Element) row.query("td").get(0);
            cssClass = cell.getAttributeValue("class");
            value = cell.getValue();
        }
        Check.notNull(cssClass, "cssClass is null");
        Check.notNull(value, "value is null");
        return cleanWhitespace(value) + "->" + cssClass.toUpperCase();
    }

    private String cleanWhitespace(String value) {
        return value.replace('\n', ' ').replace((char)160, ' ').trim();
    }

    private static Collection<String> csvToCollection(String csv) {
        Collection<String> c = new ArrayList<String>();
        for (String s : csv.split(", ?")) {
            c.add(s);
        }
        return c;
    }
}

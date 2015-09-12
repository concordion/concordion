package spec.concordion.command.verifyRows.strategies;

import nu.xom.Document;
import org.concordion.api.MultiValueResult;
import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.List;

import static org.concordion.api.MultiValueResult.multiValueResult;

public class BaseMatchStrategyTest {

    public List<MultiValueResult> users;

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

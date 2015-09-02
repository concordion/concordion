package spec.concordion.command.verifyRows.strategies;

import nu.xom.Document;
import org.concordion.api.MultiValueResult;
import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.List;

import static org.concordion.api.MultiValueResult.multiValueResult;

public class BaseStrategyTest {

    public List<MultiValueResult> users;

    public boolean processFragment(String fragment, String actualData, String fragmentAfterExecution) throws Exception {
        users = parse(actualData);

        Document document = new TestRig()
                .withFixture(this)
                .processFragment(fragment)
                .getXOMDocument();

        String fragmentExecuted = document.getRootElement().query("//table").get(0).toXML();

        return clearFormatting(fragmentExecuted).equals(clearFormatting(fragmentAfterExecution));
    }

    private String clearFormatting(String formatted) {
        StringBuilder unformatted = new StringBuilder(1024);
        int length = formatted.length();
        for (int i = 0; i < length; i++) {
            char c = formatted.charAt(i);
            if (!Character.isWhitespace(c)) {
                unformatted.append(c);
            }
        }
        return unformatted.toString();
    }

    private List<MultiValueResult> parse(String actual) {
        List<MultiValueResult> result = new ArrayList<MultiValueResult>();
        for (String single : actual.replace('\n', ' ').trim().split(";")) {
            result.add(parseSingle(single));
        }
        return result;
    }

    private MultiValueResult parseSingle(String single) {
        String[] words = single.substring(1, single.length()-1).split(",");
        return multiValueResult()
                .with("firstName", words[0])
                .with("lastName", words[1])
                .with("age", words[2]);
    }
}

package spec.concordion.common.command.verifyRows;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class TableBodySupportTest {

    private List<String> names = new ArrayList<String>();

    public void setUpNames(String namesAsCSV) {
        for (String name : namesAsCSV.split(", *")) {
            names.add(name);
        }
    }
    
    public List<String> getNames() {
        return names;
    }

    public String process(String inputFragment) {
        return new TestRig()
            .withFixture(this)
            .processFragment(inputFragment)
            .getXOMDocument()
            .query("//table").get(0)
            .toXML()
            .replaceAll("\u00A0", "&#160;");
    }}

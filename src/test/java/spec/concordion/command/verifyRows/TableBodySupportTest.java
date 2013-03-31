package spec.concordion.command.verifyRows;

import java.util.ArrayList;
import java.util.List;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class TableBodySupportTest extends ConcordionTestCase {

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

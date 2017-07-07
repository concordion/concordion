package spec.concordion.command.verifyRows.results;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class SurplusRowsTest extends MissingRowsTest {

    public void addPerson(String firstName, String lastName) {
        super.addPerson(firstName, lastName, 1973);
    }

}

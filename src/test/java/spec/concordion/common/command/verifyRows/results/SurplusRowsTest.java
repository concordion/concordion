package spec.concordion.common.command.verifyRows.results;

import org.concordion.api.ConcordionFixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class SurplusRowsTest extends MissingRowsTest {

    public void addPerson(String firstName, String lastName) {
        super.addPerson(firstName, lastName, 1973);
    }

}

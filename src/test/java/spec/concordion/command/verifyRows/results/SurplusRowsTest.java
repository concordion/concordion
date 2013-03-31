package spec.concordion.command.verifyRows.results;

public class SurplusRowsTest extends MissingRowsTest {

    public void addPerson(String firstName, String lastName) {
        super.addPerson(firstName, lastName, 1973);
    }

}

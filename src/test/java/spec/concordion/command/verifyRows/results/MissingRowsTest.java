package spec.concordion.command.verifyRows.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class MissingRowsTest extends ConcordionTestCase {
    private List<Person> people = new ArrayList<Person>();

    public void addPerson(String firstName, String lastName, int birthYear) {
        people.add(new Person(firstName, lastName, birthYear));
    }
    
    public String getOutputFragment(String inputFragment) {
        return new TestRig()
            .withFixture(this)
            .processFragment(inputFragment)
            .getXOMDocument()
            .query("//table").get(0)
            .toXML()
            .replaceAll("\u00A0", "&#160;");
    }
    
    public Collection<Person> getPeople() {
        return people;
    }
    
    class Person {
        
        public Person(String firstName, String lastName, int birthYear) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthYear = birthYear;
        }
        
        public String firstName;
        public String lastName;
        public int birthYear;
    }
}

package spec.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.concordion.api.ExpectedToPass;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@ExpectedToPass
@RunWith(ConcordionRunner.class)
public class SpikeTest extends ConcordionTestCase {

    public String getGreetingFor(String name) {
        return "Hello " + name + "!";
    }
    
    public void doSomething() {
    }
    
    @SuppressWarnings("serial")
    public Collection<Person> getPeople() {
        return new ArrayList<Person>() {{
            add(new Person("John", "Travolta"));
//            add(new Person("Frank", "Zappa"));
        }};
        
    }
    
    class Person {
        
        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        public String firstName;
        public String lastName;
    }    
}

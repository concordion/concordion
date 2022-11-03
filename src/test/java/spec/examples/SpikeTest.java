package spec.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.ExpectedToPass;

@ExpectedToPass
// @RunWith(ConcordionRunner.class)
@ConcordionFixture
public class SpikeTest {

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

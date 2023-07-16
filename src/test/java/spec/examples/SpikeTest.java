package spec.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.ExpectedToPass;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@ExpectedToPass
#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
public class SpikeTest {

    public String getGreetingFor(String name) {
        return "Hello " + name + "!";
    }
    
    public void doSomething() {
    }
    
    @SuppressWarnings("serial")
    public Collection<Person> getPeople() {
        return new ArrayList<Person>() {{
//            #if JUNIT_VINTAGE
            add(new Person("John", "Travolta"));
//            #else
//            add(new Person("Frank", "Zappa"));
//            #endif
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

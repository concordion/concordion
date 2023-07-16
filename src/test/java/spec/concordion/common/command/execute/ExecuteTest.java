package spec.concordion.common.command.execute;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.concordion.api.ConcordionFixture;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

#if JUNIT_VINTAGE
@RunWith(ConcordionRunner.class)
#else
@ConcordionFixture
#endif
@FullOGNL
public class ExecuteTest {

    private boolean myMethodWasCalled = false;
	private String argument = null;
    
    public boolean myMethodWasCalledProcessing(String fragment) {
    	myMethodWasCalled = false;
    	argument = null;
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        return myMethodWasCalled;
    }
    
    public String getArgument() {
    	return argument;
    }
    
    public void myMethod() {
    	argument = "none";
        myMethodWasCalled = true;
    }
    
    public void myMethod(String arg) {
        myMethodWasCalled = true; 	
        argument = arg;
    }
    
    public void myMethod(Map<String, String> arg) {
        myMethodWasCalled = true; 	
        
        StringBuilder args = new StringBuilder();
        
        Set<String> keys  = arg.keySet();
        Set<String> orderedKeys = new TreeSet<String>(keys);
        
        args.append("{");
        for (String key: orderedKeys) {
        	args.append("(").append(key).append(":").append(arg.get(key)).append(")");
        }
        args.append("}");
        
        argument = args.toString();
    }
    
    public void myMethod(List<String> arg) {
        myMethodWasCalled = true; 	
        
        
        StringBuilder args= new StringBuilder();
        
        args.append("[");
        for (String item: arg) {
        	args.append("(").append(item).append(")");
        }
        args.append("]");
        
        argument = args.toString();
    }

}

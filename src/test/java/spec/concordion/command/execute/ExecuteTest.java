package spec.concordion.command.execute;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.concordion.integration.junit3.ConcordionTestCase;

import test.concordion.TestRig;

public class ExecuteTest extends ConcordionTestCase {

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

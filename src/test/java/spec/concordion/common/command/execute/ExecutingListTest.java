package spec.concordion.common.command.execute;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.MultiValueResult;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ExecutingListTest {
	
	private List<MultiValueResult> treeEntries = new ArrayList<MultiValueResult>();
	
	public void parseNode(String text, int level) {
		treeEntries.add(new MultiValueResult()
							.with("name", text)
							.with("level", level));
	}
	
	public List<MultiValueResult> getNodes() {
		return treeEntries;
	}
	
	public void process(String fragment) throws Exception {
		new TestRig()
        	.withFixture(this)
        	.processFragment(fragment);
	}
	
}

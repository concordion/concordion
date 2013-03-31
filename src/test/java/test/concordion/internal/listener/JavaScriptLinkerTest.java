package test.concordion.internal.listener;

import static org.junit.Assert.assertEquals;
import nu.xom.Document;
import nu.xom.Element;

import org.concordion.api.Resource;
import org.concordion.internal.listener.JavaScriptLinker;
import org.junit.Test;


public class JavaScriptLinkerTest {

	private static final Resource NOT_NEEDED_PARAMETER = null;

	@Test // See Issue 26
	public void xmlOutputContainsAnExplicitEndTagForScriptElement() {
		JavaScriptLinker javaScriptLinker = new JavaScriptLinker(NOT_NEEDED_PARAMETER);
		
		Element html = new Element("html");
		Element head = new Element("head");
		html.appendChild(head);
		
		javaScriptLinker.beforeParsing(new Document(html));
		
		assertEquals("<head><script type=\"text/javascript\"></script></head>", head.toXML());
	}
}

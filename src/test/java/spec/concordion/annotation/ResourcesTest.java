package spec.concordion.annotation;

import java.io.File;
import java.net.URISyntaxException;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ResourcesTest {
    private TestRig testRig = null;
	private JavaSourceCompiler compiler;
	private ProcessingResult result;
	
    public void process(String javaFragment) throws Exception {
        compiler = new JavaSourceCompiler();
        String htmlFragment = "";
        Object fixture = compile(javaFragment);
        
        result = process(htmlFragment, fixture);
    }

    public void process(String javaFragment, String javaFragmentParent) throws Exception {
        compiler = new JavaSourceCompiler();
        String htmlFragment = "";
        Object fixture = compile(javaFragment, javaFragmentParent);
        
        result = process(htmlFragment, fixture);
    }
    
    public boolean processExpectingException(String javaFragment, String errorMessage) throws Exception {
    	try {
    		process(javaFragment);
    	} catch (Exception e) {
    		return e.getMessage().contains(errorMessage);
    	}
        
        return false;
    }
    
    private Object compile(String javaSource) throws Exception, InstantiationException, IllegalAccessException {
        return compiler.compile(javaSource).newInstance();
	}
    
    private Object compile(String javaSource, String javaSourceParent) throws Exception, InstantiationException, IllegalAccessException {
        return compiler.compileWithParent(javaSource, javaSourceParent).newInstance();
	}

	private ProcessingResult process(String htmlFragment, Object fixture) {
		testRig = new TestRig();
        ProcessingResult result = testRig
            .withFixture(fixture)
            .withResource(new Resource("/demo.css"), "")
            .withResource(new Resource("/spec/demo.js"), "")
            .withResource(new Resource("/spec/demo.txt"), "")
            .withResource(new Resource("/resources/test/demo.css"), "")
            .processFragment(htmlFragment);
        
        return result;
    }
	
    public String getLink(String expectedResource) {
    	Element[] links = result.getRootElement().getFirstChildElement("head").getChildElements("link");
    	
    	for (Element link : links) {
			if (link.getAttributeValue("href").contains(expectedResource)) {
				return expectedResource;
			}
		}
    	
    	return "";
    }
    
    public String getScript(String expectedResource) {
    	Element[] scripts = result.getRootElement().getFirstChildElement("head").getChildElements("script");
    	
    	for (Element script : scripts) {
			if (script.getAttributeValue("src").contains(expectedResource)) {
				return expectedResource;
			}
		}
    	
    	return "";
    }
    
    public boolean isCssIncluded(String expectedResource) {
    	Element[] styles = result.getRootElement().getFirstChildElement("head").getChildElements("style");
    	
    	for (Element style : styles) {
			if (style.getText().contains("Here is a CSS file")) {
				return true;
			}
		}
    	
    	return false;
    }
    
    public boolean isDefaultCssIncluded() {
    	Element[] styles = result.getRootElement().getFirstChildElement("head").getChildElements("style");
    	
    	for (Element style : styles) {
			if (style.getText().contains(".example {")) {
				return true;
			}
		}
    	
    	return false;
    }
    
    
    public boolean isCopied(String expectedResource) {
    	File root = null;
    	
    	try {
			root = new File(this.getClass().getClassLoader().getResource("").toURI());
		} catch (URISyntaxException e) {
			// Ignore
		}
    	
    	File file = new File(root, expectedResource);
    	    	
		return file.exists();
    }
}

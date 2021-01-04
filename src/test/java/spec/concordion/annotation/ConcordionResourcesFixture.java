package spec.concordion.annotation;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ConcordionResourcesFixture {
    private TestRig testRig = null;
	private JavaSourceCompiler compiler;
	private ProcessingResult result;
	
    public void process(String javaFragment) throws Exception {
        compiler = new JavaSourceCompiler();
        String htmlFragment = "";
        Object fixture = compile(javaFragment);
        
        result = process("", htmlFragment, fixture);
    }

    public void process(String javaFragment, String javaFragmentParent) throws Exception {
        compiler = new JavaSourceCompiler();
        String htmlFragment = "";
        Object fixture = compile(javaFragment, javaFragmentParent);
        
        result = process("", htmlFragment, fixture);
    }
    
    public boolean processExpectingException(String javaFragment, String errorMessage) throws Exception {
    	try {
    		process(javaFragment);
    	} catch (Exception e) {
    		return e.getMessage().contains(errorMessage);
    	}
        
        return false;
    }
    
    public void processSpec(String htmlHead, String javaFragment) throws InstantiationException, IllegalAccessException, Exception {
    	Object fixture = compile(javaFragment);
        
        result = process(htmlHead, "", fixture);
    }
    
    private Object compile(String javaSource) throws Exception, InstantiationException, IllegalAccessException {
        return compiler.compile(javaSource).newInstance();
	}
    
    private Object compile(String javaSource, String javaSourceParent) throws Exception, InstantiationException, IllegalAccessException {
        return compiler.compileWithParent(javaSource, javaSourceParent).newInstance();
	}

	private ProcessingResult process(String htmlHead, String htmlFragment, Object fixture) {
		testRig = new TestRig();
        ProcessingResult result = testRig
            .withFixture(fixture)
            .withResource(new Resource("/resources.css"), "")
            .withResource(new Resource("/resources/test/resources.js"), "")
            .withResource(new Resource("/resources/test/resources.txt"), "")
            .withResource(new Resource("/resources/test/resources with space.txt"), "")
            .withResource(new Resource("/resources/test/subfolder/resources with space.js"), "")
            .withResource(new Resource("/resources/test/../../resources.css"), "")
            .withResource(new Resource("/resources/test/missingresourcesfolder/../../../resources.css"), "")
            .processFragment("/resources/test/ResourcesTest.html", htmlHead, htmlFragment);
        
        return result;
    }
	
	public Iterable<String> getResources() {
		List<String> resources = new ArrayList<String>();
    	
    	List<Resource> resourceList = testRig.getCopiedResources();
    	for (Resource resource : resourceList) {
			resources.add(new Resource("/resources/test/ResourcesTest.html").getRelativePath(resource));
		}
    	
    	return resources;
	}
	
	public boolean getLinkExists(String expectedResource) {
    	Element[] links = result.getRootElement().getFirstChildElement("head").getChildElements("link");
		    	    	
    	for (Element link : links) {
			if (link.getAttributeValue("href").equals(expectedResource)) {
				return true;
			}
		}
    	
    	return false;
    }

	public boolean getScriptExists(String expectedResource) {
    	Element[] scripts = result.getRootElement().getFirstChildElement("head").getChildElements("script");
    	
    	for (Element script : scripts) {
			if (script.getAttributeValue("src").equals(expectedResource)) {
				return true;
			}
		}
    	
    	return false;
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
			if (style.getText().contains("[concordion\\:example] {")) {
				return true;
			}
		}
    	
    	return false;
    }
        
    public boolean isCopied(String expectedResource) {
    	if (!expectedResource.startsWith("/")) {
    		expectedResource = "/" + expectedResource;
    	}
    	
    	return testRig.hasCopiedResource(new Resource(expectedResource));
    }
}

package spec.concordion.annotation;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.Resource;
import org.concordion.api.Element;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class ResourcesTest {
	private TestRig testRig = null;
	private JavaSourceCompiler compiler;
	private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("class\\s*(.*?)\\s*(\\{|extends)");
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
    	return compiler.compile(getClassName(javaSource), javaSource).newInstance();
	}
    
    private Object compile(String javaSource, String javaSourceParent) throws Exception, InstantiationException, IllegalAccessException {
    	return compiler.compile(getClassName(javaSource), javaSource, getClassName(javaSourceParent), javaSourceParent).newInstance();
	}

	private ProcessingResult process(String htmlFragment, Object fixture) {
		testRig = new TestRig();
        ProcessingResult result = testRig
            .withFixture(fixture)
            .withResource(new Resource("/demo.css"), "")
            .withResource(new Resource("/spec/demo.js"), "")
            .withResource(new Resource("/spec/demo.txt"), "")
            .processFragment(htmlFragment);
        
        return result;
    }
	
    private String getClassName(String javaFragment) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(javaFragment);
        matcher.find();
        
        return matcher.group(1);
    }
    
//    private String getPackageName(String javaFragment) {
//    	Matcher matcher = Pattern.compile("(?m)^package\\s*(.*);$").matcher(javaFragment);
//        //Matcher matcher = PACKAGE_NAME_PATTERN.matcher(javaFragment);
//        matcher.find();
//        
//        String packageName = matcher.group(1);
//        
//        if (!packageName.isEmpty()) {
//        	packageName += ".";
//        }
//        
//        return packageName;
//    }
    
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

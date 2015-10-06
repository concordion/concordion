package spec.concordion.annotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.extension.fake.FakeExtensionBase;

@RunWith(ConcordionRunner.class)
public class ResourcesTest {
	
	private JavaSourceCompiler compiler;
	private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("class\\s*(.*?)\\s*(\\{|extends)");
	
    public String process(String javaFragment) throws Exception {
        compiler = new JavaSourceCompiler();
        String htmlFragment = "";
        Object fixture = compile(javaFragment);
        ProcessingResult result = process(htmlFragment, fixture);
        
        return result.getRootElement().getAttributeValue(FakeExtensionBase.FAKE_EXTENSION_ATTR_NAME);
    }

	private ProcessingResult process(String htmlFragment, Object fixture) {
        ProcessingResult result = new TestRig()
            .withFixture(fixture)
            .processFragment(htmlFragment);
        return result;
    }
	
    private Object compile(String javaSource) throws Exception, InstantiationException, IllegalAccessException {
    	return compiler.compile(getClassName(javaSource), javaSource).newInstance();
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
    
    public String getResource(String resource) {
    	return resource;
    }
}

package spec.concordion.annotation;

import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.FixtureInstance;
import org.junit.runner.RunWith;
import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(ConcordionRunner.class)
public class FlexmarkOptions {
    private JavaSourceCompiler compiler;
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public class\\s*(.*?)\\s*(\\{|extends)");

    public String process(String markdown, String javaFragment) throws Exception {
        compiler = new JavaSourceCompiler();
        Object fixture = compile(javaFragment);
        ProcessingResult result = new TestRig()
                .withFixture(fixture)
                .loadFlexmarkOptionsFrom(new FixtureInstance(fixture))
                .withResource(new Resource("/spec.md"), markdown)
                .processMarkdownFragment(markdown);
        return result.getOutputFragmentParagraphXML();
    }

    private Object compile(String javaSource) throws Exception, InstantiationException,
            IllegalAccessException {
        return compiler.compile(getClassName(javaSource), javaSource).newInstance();
    }

    private String getClassName(String javaFragment) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(javaFragment);
        matcher.find();
        return matcher.group(1);
    }
}

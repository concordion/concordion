package test.concordion.internal;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.concordion.internal.extension.ExtensionInitialisationException;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.junit.Test;

import test.concordion.JavaSourceCompiler;
import test.concordion.extension.fake.FakeExtension1;
import test.concordion.extension.fake.FakeExtension2;

@SuppressWarnings({"rawtypes","unchecked"})
public class FixtureExtensionLoaderWithAnnotatedFieldsTest {
    private JavaSourceCompiler compiler = new JavaSourceCompiler();
    private FixtureExtensionLoader loader = new FixtureExtensionLoader();

    @Test
    public void loadsPublicFieldWithExtensionAnnotation() throws Exception {
        String fields = 
            "@Extension " +
            "public FakeExtension1 extension = new FakeExtension1();";
        
        List extensions = loader.getExtensionsForFixture(withFieldDeclaration(fields));
        
        assertThat((List<Object>)extensions, hasItem(instanceOf(FakeExtension1.class)));
    }
    
    @Test
    public void loadsPublicFieldFromSuperClassWithExtensionAnnotation() throws Exception {
        String superClassFields = 
            "@Extension " +
            "public ConcordionExtension extension = new FakeExtension2();";
        
        classWithFieldDeclaration(superClassFields, "BaseFixture", null);
        List extensions = loader.getExtensionsForFixture(withFieldDeclaration("", "BaseFixture"));
        
        assertThat((List<Object>)extensions, hasItem(instanceOf(FakeExtension2.class)));
    }
    
    @Test
    public void ignoresFieldsWithoutExtensionAnnotation() throws Exception {
        String fields = 
            "public ConcordionExtension extension = new FakeExtension1();";
        
        List extensions = loader.getExtensionsForFixture(withFieldDeclaration(fields));
        
        assertThat(extensions.size(), equalTo(0));
    }

    @Test
    public void errorsIfPrivateFieldHasExtensionAnnotation() throws Exception {
        String fields = 
            "@Extension " +
            "private ConcordionExtension extension = new FakeExtension1();";
        
        try {
            loader.getExtensionsForFixture(withFieldDeclaration(fields));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), containsString("must be public"));
        }
    }
    
    @Test
    public void errorsIfProtectedFieldHasExtensionAnnotation() throws Exception {
        String fields = 
            "@Extension " +
            "protected ConcordionExtension extension = new FakeExtension1();";
        
        try {
            loader.getExtensionsForFixture(withFieldDeclaration(fields));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), containsString("must be public"));
        }
    }
    
    @Test
    public void errorsIfPackageAccessibleFieldHasExtensionAnnotation() throws Exception {
        String fields = 
            "@Extension " +
            "ConcordionExtension extension = new FakeExtension1();";
        
        try {
            loader.getExtensionsForFixture(withFieldDeclaration(fields));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), containsString("must be public"));
        }
    }
    
    @Test
    public void errorsIfFieldWithExtensionAnnotationIsNull() throws Exception {
        String fields = 
            "@Extension " +
            "public ConcordionExtension badExtension = null;";
        
        try {
            loader.getExtensionsForFixture(withFieldDeclaration(fields));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), containsString("must be non-null"));
        }
    }
    
    @Test
    public void errorsIfFieldWithExtensionAnnotationIsNotAConcordionExtension() throws Exception {
        String fields = 
            "@Extension " +
            "public String notAnExtension = \"foo\";";
        
        try {
            loader.getExtensionsForFixture(withFieldDeclaration(fields));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), containsString("must implement org.concordion.api.extension.ConcordionExtension"));
        }
    }
    
    private Object withFieldDeclaration(String declaration) throws Exception, InstantiationException,
            IllegalAccessException {
        return withFieldDeclaration(declaration, null);
    }

    private Object withFieldDeclaration(String declaration, String superClassName) throws Exception, InstantiationException,
            IllegalAccessException {
        String className = "ExampleFixture";
        Class<?> clazz = classWithFieldDeclaration(declaration, className, superClassName);
        Object fixture = clazz.newInstance();
        return fixture;
    }
    
    private Class<?> classWithFieldDeclaration(String declaration, String className, String superClassName) throws Exception {
        String code = 
            "import org.concordion.api.extension.Extension;" +
            "import org.concordion.api.extension.ConcordionExtension;" +
            "import org.concordion.api.extension.ConcordionExtender;" +
            "import test.concordion.extension.fake.*;" +
            "public class " + className + (superClassName != null ? " extends " + superClassName : "") + " {" +
            declaration +
            "}";    
        Class<?> clazz = compiler.compile(className, code);
        return clazz;
    }
}

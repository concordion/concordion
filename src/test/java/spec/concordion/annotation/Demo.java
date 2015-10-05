package spec.concordion.annotation;

import org.concordion.api.CopyResource;
import org.concordion.api.CopyResource.InsertType;

//@RunWith(ConcordionRunner.class)
@CopyResource(sourceFiles = { "*.css", "demo.txt" }, insertType = InsertType.LINKED)
public class Demo extends DemoParent {
    
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
//    
//    @Test
//    public void anotherTest() {
//        System.out.println("another test");
//    }
//    
//    @Before
//    public void before() {
//        System.out.println("before");
//    }
//    
//    @After
//    public void after() {
//        System.out.println("after");
//    }
//    
//    @BeforeClass
//    public static void beforeClass() {
//        System.out.println("beforeClass");
//    }
//    
//    @AfterClass
//    public static void afterClass() {
//        System.out.println("afterClass");
//    }
}

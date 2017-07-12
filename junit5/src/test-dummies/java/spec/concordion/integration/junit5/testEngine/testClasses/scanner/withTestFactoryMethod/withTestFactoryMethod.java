package spec.concordion.integration.junit5.testEngine.testClasses.scanner.withTestFactoryMethod;

import org.junit.jupiter.api.TestFactory;
import spec.concordion.integration.junit5.ConcordionJUnit5;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by tim on 12/07/17.
 */
@ConcordionJUnit5
public class withTestFactoryMethod {
    @TestFactory
    Collection<Object> methodWithTestFactoryAnnotation() {
        return Collections.emptyList();
    }
}

package spec.concordion.integration.junit5.testEngine;

import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 * Created by tim on 12/07/17.
 */
public class ConcordionTestDescriptor extends AbstractTestDescriptor {
    private Class<?> clas;

    public ConcordionTestDescriptor(UniqueId uniqueId, Class<?> clas) {
        super(uniqueId, clas.getCanonicalName());
        this.clas = clas;
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }
}

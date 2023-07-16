package org.concordion.integration.junit.platform.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 * An {@code EngineDescriptor} for {@link ConcordionTestEngine}.
 *
 * @see SpecificationDescriptor
 * @see ExampleDescriptor
 * @since 4.0
 */
public class ConcordionEngineDescriptor extends EngineDescriptor implements Node<ConcordionEngineExecutionContext> {

    public ConcordionEngineDescriptor(UniqueId uniqueId, String displayName) {
        super(uniqueId, displayName);
    }

}

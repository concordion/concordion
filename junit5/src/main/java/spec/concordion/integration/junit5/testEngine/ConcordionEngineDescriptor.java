package spec.concordion.integration.junit5.testEngine;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

/**
 * Created by tim on 12/07/17.
 */
public class ConcordionEngineDescriptor extends EngineDescriptor {


    private EngineDiscoveryRequest discoveryRequest;

    public ConcordionEngineDescriptor(UniqueId uniqueId, String id, EngineDiscoveryRequest discoveryRequest) {
        super(uniqueId, id);
        this.discoveryRequest = discoveryRequest;

        new ConcordionTestEngineScanner()
                .scan("")
                .stream()
                .filter(this::filterForDiscoveryRequest)
                .forEach(this::addClasstoChild);
    }

    private boolean filterForDiscoveryRequest(Class<?> aClass) {
        return true;
    }

    private void addClasstoChild(Class<?> aClass) {
        UniqueId newId = getUniqueId().append("className", aClass.getCanonicalName());
        TestDescriptor descriptor = new ConcordionTestDescriptor(newId, aClass);
        addChild(descriptor);
    }
}

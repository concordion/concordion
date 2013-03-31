package test.concordion.extension;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;

public class DynamicResourceExtension implements ConcordionExtension, ConcordionBuildListener {

    public static final String SOURCE_PATH = "/test/concordion/o.png";
    private Target target;

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withBuildListener(this);
    }

    public void concordionBuilt(ConcordionBuildEvent event) {
        this.target = event.getTarget();
        
        createResourceInTarget();  // NOTE: normally this would be done during specification processing - eg in an AssertEqualsListener 
    }

    private void createResourceInTarget() {
        try {
            target.copyTo(new Resource("/resource/my.txt"), new ByteArrayInputStream("success".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

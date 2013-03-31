package test.concordion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.internal.util.Check;

public class StubTarget implements Target {

    private final LinkedHashMap<Resource, String> writtenStrings = new LinkedHashMap<Resource, String>();
    private final List<Resource> copiedResources = new ArrayList<Resource>();
    
    public void copyTo(Resource resource, InputStream inputStream) throws IOException {
        copiedResources.add(resource);
    }

    public void delete(Resource resource) throws IOException {
    }

    public void write(Resource resource, String s) throws IOException {
        writtenStrings.put(resource, s);
    }
    
    public String getWrittenString(Resource resource) {
        Check.isTrue(writtenStrings.containsKey(resource), "Expected resource '" + resource.getPath() + "' was not written to target");
        return writtenStrings.get(resource);
    }
    
    public boolean exists(Resource resource) {
        return hasCopiedResource(resource) || writtenStrings.containsKey(resource);
    }

    public boolean hasCopiedResource(Resource resource) {
        return copiedResources.contains(resource);
    }

    public OutputStream getOutputStream(Resource resource) {
        throw new UnsupportedOperationException("not implemented on StubTarget");
    }
}

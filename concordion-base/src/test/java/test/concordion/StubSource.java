package test.concordion;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.internal.ClassPathSource;
import org.concordion.internal.util.Check;

public class StubSource implements Source {

    // FUTURE: Could change this to byte[] instead of String - to handle binary resources.
    private Map<Resource, String> resources = new HashMap<Resource, String>();

    public void addResource(String resourceName, String content) {
        addResource(new Resource(resourceName), content);
    }

    public void addResource(Resource resource, String content) {
        resources.put(resource, content);
    }

    public InputStream createInputStream(Resource resource) throws IOException {
        Check.isTrue(canFind(resource), "Resource '" + resource.getPath() + "' has not been stubbed in the simulator.");
        return new ByteArrayInputStream(resources.get(resource).getBytes("UTF-8"));
    }

    public boolean canFind(Resource resource) {
        return resources.containsKey(resource);
    }
    
    @Override
    public String toString() {
        return "stub";
    }

    @Override
    public String readAsString(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented for stub source");
    }

    // This is a real implementation so it can add the CSS styling to the test output
    @Override
    public String readResourceAsString(String resourcePath) {
        try {
            ClassLoader classLoader = ClassPathSource.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream(resourcePath.replaceFirst("/", ""));
            if (in == null) {
                throw new IOException("Resource not found");
            }
            Reader reader = new InputStreamReader(in, "UTF-8");
            try {
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader bufferedReader = new BufferedReader(reader);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource '" + resourcePath + "'", e);
        }
    }
}

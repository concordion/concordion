package test.concordion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Resource;
import org.concordion.internal.util.Check;

public class FileOutputStreamer implements OutputStreamer {

    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private File baseOutputDir;
    private List<String> resourcesOutput = new ArrayList<String>();

    public FileOutputStreamer() {
        baseOutputDir = getBaseOutputDir();
    }
    
    @Override
    public OutputStream getOutputStream(Resource resource) throws IOException {
        Check.notNull(resource, "resource is null");
        mkdirs(resource);
        return createOutputStream(resource);
    }

    private OutputStream createOutputStream(Resource resource) throws FileNotFoundException {
        File file = getFile(resource);
        resourcesOutput.add(resource.getPath());
        return new FileOutputStream(file);
    }
    
    public File getFile(Resource resource) {
        return new File(baseOutputDir, resource.getPath());
    }
    
    private void mkdirs(Resource resource) throws IOException {
        File dir = getFile(resource).getParentFile();
        dir.mkdirs();
    }
    
    public File getBaseOutputDir() {
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "concordion");
        }
        return new File(outputPath);
    }

    public List<String> getResourcesOutput() {
        return resourcesOutput;
    }
}

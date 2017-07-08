package spec.concordion.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.internal.FileTarget;

/**
 * For test purposes only. For real usage, create an implementation - eg. 
 * <a href="https://github.com/concordion/concordion-excel-extension/blob/master/src/main/java/org/concordion/ext/excel/ExcelSuffixRenamingTarget.java">
 * ExcelSuffixRenamingTarget</a>.
 */
public class FileTargetWithSuffix implements Target {

    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private String suffix;
    private FileTarget target;
    
    public FileTargetWithSuffix(String suffix) {
        this.suffix = suffix;
        target = new FileTarget(getBaseOutputDir());
    }
    
    private File getBaseOutputDir() {
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "concordion");
        }
        return new File(outputPath);
    }

    
    public void write(Resource resource, String s) throws IOException {
        target.write(rename(resource), s);
    }

    public void copyTo(Resource resource, InputStream inputStream) throws IOException {
        target.copyTo(rename(resource), inputStream);
    }

    public OutputStream getOutputStream(Resource resource) throws IOException {
        return target.getOutputStream(rename(resource));
    }

    public void delete(Resource resource) throws IOException {
        target.delete(rename(resource));
    }

    public boolean exists(Resource resource) {
        return target.exists(rename(resource));
    }
    
    public String resolvedPathFor(Resource resource) {
        return target.resolvedPathFor(rename(resource));
    }

    private Resource rename(Resource resource) {
        return new Resource(resource.getPath().replaceFirst("\\..*$", "\\." + suffix));
    }
}

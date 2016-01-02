package org.concordion.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.internal.util.IOUtil;

public class FileTargetWithSuffix implements Target {

    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private String suffix;
    private FileTarget target;
    
    public FileTargetWithSuffix(String suffix, IOUtil ioUtil) {
        this.suffix = suffix;
        target = new FileTarget(getBaseOutputDir(), ioUtil);
    }
    
    private File getBaseOutputDir() {
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "concordion");
        }
        return new File(outputPath);
    }

    
    @Override
    public void write(Resource resource, String s) throws IOException {
        target.write(rename(resource), s);
    }

    @Override
    public void copyTo(Resource resource, InputStream inputStream) throws IOException {
        target.copyTo(rename(resource), inputStream);
    }

    @Override
    public OutputStream getOutputStream(Resource resource) throws IOException {
        return target.getOutputStream(rename(resource));
    }

    @Override
    public void delete(Resource resource) throws IOException {
        target.delete(rename(resource));
    }

    @Override
    public boolean exists(Resource resource) {
        return target.exists(rename(resource));
    }
    
    @Override
    public String resolvedPathFor(Resource resource) {
        return target.resolvedPathFor(rename(resource));
    }

    private Resource rename(Resource resource) {
        return new Resource(resource.getPath().replaceFirst("\\..*$", "\\." + suffix));
    }
}

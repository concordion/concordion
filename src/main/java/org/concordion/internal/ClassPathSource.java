package org.concordion.internal;

import java.io.IOException;
import java.io.InputStream;

import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.internal.util.IOUtil;

public class ClassPathSource implements Source {

    public InputStream createInputStream(Resource resource) throws IOException {
        InputStream inputStream = IOUtil.getResourceAsStream(resource.getPath());
        if (inputStream == null) {
            throw new IOException(String.format("Resource '[%s: %s]' not found", this, resource.getPath()));
        }
        return inputStream;
    }

    public boolean canFind(Resource resource) {
        InputStream stream = IOUtil.getResourceAsStream(resource.getPath());
        if (stream == null) {
            return false;
        }
        try {
            stream.close();
        } catch (IOException e) {
            // Ignore
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "classpath";
    }
}

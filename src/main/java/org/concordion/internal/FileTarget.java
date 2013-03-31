package org.concordion.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;

public class FileTarget implements Target {

    private static final long FRESH_ENOUGH_MILLIS = 2000; // 2 secs
    private final File baseDir;

    public FileTarget(File baseDir) {
        this.baseDir = baseDir;
    }
    
    public void copyTo(Resource resource, InputStream inputStream) throws IOException {
        Check.notNull(resource, "resource is null");
        mkdirs(resource);
        File outputFile = getFile(resource);
        // Do not overwrite if a recent copy already exists
        if (outputFile.exists() && isFreshEnough(outputFile)) {
            return;
        }
        IOUtil.copy(inputStream, createOutputStream(resource));
    }

    public void delete(Resource resource) throws IOException {
        Check.notNull(resource, "resource is null");
        getFile(resource).delete();
    }

    public void write(Resource resource, String s) throws IOException {
        Check.notNull(resource, "resource is null");
        mkdirs(resource);
        Writer writer = new BufferedWriter(createWriter(resource, "UTF-8"));
        try {
            writer.write(s);
        } finally {
            writer.close();
        }
    }

    private OutputStreamWriter createWriter(Resource resource, String encoding) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(getFile(resource)), encoding);
    }

    public File getFile(Resource resource) {
        return new File(baseDir, resource.getPath());
    }
    
    public OutputStream getOutputStream(Resource resource) throws IOException {
        Check.notNull(resource, "resource is null");
        mkdirs(resource);
        return createOutputStream(resource);
    }

    private OutputStream createOutputStream(Resource resource) throws FileNotFoundException {
        return new FileOutputStream(getFile(resource));
    }
    
    private void mkdirs(Resource resource) throws IOException {
        File dir = getFile(resource).getParentFile();
        dir.mkdirs();
    }
    
    private boolean isFreshEnough(File outputFile) {
        long ageInMillis = Math.abs(outputFile.lastModified() - System.currentTimeMillis());
        return ageInMillis < FRESH_ENOUGH_MILLIS;
    }

    public boolean exists(Resource resource) {
        return getFile(resource).exists();        
    }
}

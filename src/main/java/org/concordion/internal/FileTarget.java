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

public class FileTarget implements Target {

    private static final long FRESH_ENOUGH_MILLIS = 2000; // 2 secs
    private static final int BUFFER_SIZE = 4096;
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
        OutputStream outputStream = createOutputStream(resource);
        try {
            copy(inputStream, outputStream);
        } finally {
            outputStream.close();
        }
    }

    public void delete(Resource resource) throws IOException {
        Check.notNull(resource, "resource is null");
        getFile(resource).delete();
    }

    public void write(Resource resource, String s) throws IOException {
        Check.notNull(resource, "resource is null");
        mkdirs(resource);
        FileOutputStream os = new FileOutputStream(getFile(resource));
        Writer writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        try {
            writer.write(s);
        } finally {
            writer.close();
            os.close();
        }
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
    
    public String resolvedPathFor(Resource resource) {
        return "file://" + getFile(resource).getAbsolutePath();
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }
}

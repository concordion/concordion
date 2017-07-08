package org.concordion.internal;

import java.io.*;

import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.internal.util.SimpleFormatter;

public class ClassPathSource implements Source {

    public InputStream createInputStream(Resource resource) throws IOException {
        InputStream inputStream = getResourceAsStream(resource.getPath());
        if (inputStream == null) {
            throw new IOException(SimpleFormatter.format("Resource '[%s: %s]' not found", this, resource.getPath()));
        }
        return inputStream;
    }

    public boolean canFind(Resource resource) {
        InputStream stream = getResourceAsStream(resource.getPath());
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

    public String readAsString(InputStream inputStream) throws IOException {
        return readAsString(inputStream, "UTF-8");
    }

    public String readResourceAsString(String resourcePath) {
        return readResourceAsString(resourcePath, "UTF-8");
    }

    private String readAsString(InputStream inputStream, String charsetName) throws IOException {
        Reader reader = new InputStreamReader(inputStream, charsetName);
        try {
            return readAsString(reader);
        } finally {
            reader.close();
        }
    }

    private InputStream getResourceAsStream(String resourcePath) {
        ClassLoader classLoader = ClassPathSource.class.getClassLoader();
        return classLoader.getResourceAsStream(resourcePath.replaceFirst("/", ""));
    }

    private String readAsString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private String readResourceAsString(String resourcePath, String charsetName) {
        try {
            InputStream in = getResourceAsStream(resourcePath);
            if (in == null) {
                throw new IOException("Resource not found");
            }
            Reader reader = new InputStreamReader(in, charsetName);
            try {
                return readAsString(reader);
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource '" + resourcePath + "'", e);
        }
    }
}

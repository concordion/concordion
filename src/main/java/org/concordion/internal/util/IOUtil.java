package org.concordion.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class IOUtil {
    
    private static final int BUFFER_SIZE = 4096;
    
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

    public static String readResourceAsString(String resourcePath) {
        return readResourceAsString(resourcePath, "UTF-8");
    }

    public static String readResourceAsString(String resourcePath, String charsetName) {
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
    
    public static String readAsString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static InputStream getResourceAsStream(String resourcePath) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader.getResourceAsStream(resourcePath.replaceFirst("/", ""));
    }
}

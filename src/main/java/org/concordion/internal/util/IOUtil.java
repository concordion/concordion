package org.concordion.internal.util;

import java.io.*;

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
    	ClassLoader classLoader = IOUtil.class.getClassLoader();
    	return classLoader.getResourceAsStream(resourcePath.replaceFirst("/", ""));
    }

    public static String readAsString(InputStream inputStream) throws IOException {
        return readAsString(inputStream, "UTF-8");
    }
    
    public static String readAsString(InputStream inputStream, String charsetName) throws IOException {
        Reader reader = new InputStreamReader(inputStream, charsetName);
        try {
            return readAsString(reader);
        } finally {
            reader.close();
        }
    }
}

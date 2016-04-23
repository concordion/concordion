/*
 * Copyright (c) 2010 Two Ten Consulting Limited, New Zealand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.concordion;

import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.concordion.internal.ClassPathSource;

import java.io.*;

public class FilterSource implements Source {

    private final Source source;
    private String prefix;

    public FilterSource(Source source, String filterPrefix) {
        this.source = source;
        prefix = filterPrefix;
    }

    public InputStream createInputStream(Resource resource) throws IOException {
        if (resource.getPath().startsWith(prefix)) {
            return new ByteArrayInputStream("x".getBytes("UTF-8"));
        }
        return source.createInputStream(resource);
    }

    public boolean canFind(Resource resource) {
        return resource.getName().endsWith(".html");
    }

    @Override
    public String readAsString(InputStream inputStream) throws IOException {
        return readAsString(inputStream, "UTF-8");
    }

    @Override
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

package org.concordion.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Target {

    void write(Resource resource, String s) throws IOException;

    void copyTo(Resource resource, InputStream inputStream) throws IOException;

    OutputStream getOutputStream(Resource resource) throws IOException;

    void delete(Resource resource) throws IOException;

    boolean exists(Resource resource);
}
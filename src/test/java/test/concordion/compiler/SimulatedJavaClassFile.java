package test.concordion.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class SimulatedJavaClassFile extends SimpleJavaFileObject {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public SimulatedJavaClassFile(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(getBytes());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        out.reset();
        return out;
    }

    public byte[] getBytes() {
        return out.toByteArray();
    }
}


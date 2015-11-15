package test.concordion;

import java.io.IOException;
import java.io.OutputStream;

import org.concordion.api.Resource;

public interface OutputStreamer {

    OutputStream getOutputStream(Resource resource) throws IOException;

}
package spec.concordion.extension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by tim on 6/12/15.
 */
public class ExtensionTestHelper {

    private PrintStream logStream;
    private ByteArrayOutputStream baos;

    public ExtensionTestHelper() {
        baos = new ByteArrayOutputStream(4096);
        logStream = new PrintStream(baos);
    }

    public PrintStream getLogStream() {
        return logStream;
    }

    public ByteArrayOutputStream getBaos() {
        return baos;
    }
}

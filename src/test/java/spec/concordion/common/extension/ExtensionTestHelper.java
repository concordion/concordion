package spec.concordion.common.extension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

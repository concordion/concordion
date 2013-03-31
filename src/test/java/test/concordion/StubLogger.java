package test.concordion;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;


/**
 * Spies on java.util.logging messages. 
 */
public class StubLogger {

    public ByteArrayOutputStream baos;
    public StreamHandler streamHandler;

    public StubLogger() {
        baos = new ByteArrayOutputStream(10000);
        streamHandler = new StreamHandler(baos, new SimpleFormatter());
        streamHandler.setLevel(Level.ALL);
        Logger logger = Logger.getLogger("");
        logger.addHandler(streamHandler);
    }

    /**
     * Returns any new java.util.logging messages since this method was last called, 
     * or since this object was constructed if this method has not yet been called.
     */
    public String getNewLogMessages() {
        streamHandler.flush();
        String text = baos.toString();
        baos.reset();
        return text;
    }
}

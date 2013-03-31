package test.concordion;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.rules.ExternalResource;

/**
 * Disables any java.util.logging to the console for the duration of a JUnit test.
 * <p>
 * To use this class add the following to your JUnit (4.7 or later) class:
 * <pre>
 *   &#64;Rule 
 *   public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler();  
 * </pre>
 */
public class ConsoleLogGobbler extends ExternalResource {

    private Handler savedHandler;

    @Override
    protected void before() throws Throwable {
        Logger logger = getRootLogger();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                savedHandler = handler;
                logger.removeHandler(handler);
            }
        }
    }

    @Override
    protected void after() {
        if (savedHandler != null) {
            Logger logger = getRootLogger();
            logger.addHandler(savedHandler);
        }
    }

    private Logger getRootLogger() {
        return Logger.getLogger("");
    }
}

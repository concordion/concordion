package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class LoggingExtension implements ConcordionExtension {

    private AssertLogger assertLogger = new AssertLogger();
    private ExecuteLogger executeLogger = new ExecuteLogger();
    private SetLogger setLogger = new SetLogger();
    private VerifyRowsLogger verifyRowsLogger = new VerifyRowsLogger();

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withAssertEqualsListener(assertLogger);
        concordionExtender.withAssertTrueListener(assertLogger);
        concordionExtender.withAssertFalseListener(assertLogger);
        concordionExtender.withExecuteListener(executeLogger);
        concordionExtender.withSetListener(setLogger);
        concordionExtender.withVerifyRowsListener(verifyRowsLogger);
    }
    
    public LoggingExtension withStream(PrintStream stream) {
        assertLogger.setStream(stream);
        executeLogger.setStream(stream);
        setLogger.setStream(stream);
        verifyRowsLogger.setStream(stream);
        return this;
    }
}

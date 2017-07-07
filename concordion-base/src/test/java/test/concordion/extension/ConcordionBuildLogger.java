package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.listener.*;

public class ConcordionBuildLogger implements ConcordionBuildListener {
    
    private PrintStream stream;

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void concordionBuilt(ConcordionBuildEvent event) {
        stream.println("Concordion Built");
    }
}
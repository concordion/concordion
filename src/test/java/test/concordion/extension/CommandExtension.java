package test.concordion.extension;

import java.io.PrintStream;
import java.util.List;

import org.concordion.api.*;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class CommandExtension implements ConcordionExtension {

    private PrintStream stream;
    
    public CommandExtension withStream(PrintStream stream) {
        this.stream = stream;
        return this;
    }

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withCommand("http://myorg.org/my/extension", "log", new Command() {

            public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
            }

            public void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples) {
            }

            public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            }
            
            public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture) {
                stream.println(commandCall.getElement().getText());
            }
        });
    }
}

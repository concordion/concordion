package test.concordion.extension;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.concordion.api.Command;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
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

            public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            }

            public List<CommandCall> getExamples(CommandCall command) {
                return Collections.emptyList();
            }

            public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            }
            
            public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
                stream.println(commandCall.getElement().getText());
            }

            public boolean isExample() {
                return false;
            }

            public void executeAsExample(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            }
        });
    }
}

package org.concordion.api;

import java.util.List;

public interface Command {

    void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);

    /**
     *  Used to modify the command call tree post parsing and before execution. Some things that might be done are:
     *  * Remove the example command from the parent object and put it in the examples list (or before examples)
     *  * Add new commands to examples or before examples
     *  * Modify the tree to put table or list execute commands on the right children elements.
     */
    void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples);
}

package org.concordion.api;

import java.util.List;

public interface Command {

    void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture);

    void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture);

    void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Fixture fixture);

    /**
     *  Used to modify the command call tree post parsing and before execution. Some things that might be done are:
     *  * Remove the example command from the parent object and put it in the examples list (or before examples)
     *  * Add new commands to examples or before examples
     *  * Modify the tree to put table or list execute commands on the right children elements.
     *  @param element the command call element to modify
     *  @param examples a list of examples
     *  @param beforeExamples a list of "before" examples
     */
    void modifyCommandCallTree(CommandCall element, List<ExampleCommandCall> examples, List<CommandCall> beforeExamples);
}

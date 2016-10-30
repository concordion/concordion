package org.concordion.api;

/**
 * Created by tim on 29/10/16.
 */
public class ExampleCommandCall {
    private final String exampleName;
    private final CommandCall commandCall;

    public ExampleCommandCall(String name, CommandCall command) {
        this.exampleName = name;
        this.commandCall = command;
    }

    public String getExampleName() {
        return exampleName;
    }

    public CommandCall getCommandCall() {
        return commandCall;
    }
}

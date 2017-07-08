package org.concordion.internal;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.Command;
import org.concordion.api.CommandFactory;

public class CommandRegistry implements CommandFactory {

    private Map<Object, Command> commandMap = new HashMap<Object, Command>();

    public CommandRegistry register(String namespaceURI, String commandName, Command command) {
        commandMap.put(makeKey(namespaceURI, commandName), command);
        return this;
    }
    
    public Command createCommand(String namespaceURI, String commandName) {
        return commandMap.get(makeKey(namespaceURI, commandName));
    }
    
    private Object makeKey(String namespaceURI, String commandName) {
        return namespaceURI + " " + commandName;
    }
}

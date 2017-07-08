package org.concordion.api;

public interface CommandFactory {

    Command createCommand(String namespaceURI, String commandName);
}

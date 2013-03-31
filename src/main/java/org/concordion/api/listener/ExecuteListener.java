package org.concordion.api.listener;

import java.util.EventListener;


public interface ExecuteListener extends EventListener {

    void executeCompleted(ExecuteEvent e);

}

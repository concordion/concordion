package org.concordion.api;

public class ElementUpdating {

    private static final ElementUpdating ELEMENT_UPDATING = new ElementUpdating();

    public static ElementUpdating instance() {
        return ELEMENT_UPDATING;
    }

    private boolean restrictUpdating = false;

    private ElementUpdating() {
    }

    public void restrict() {
        restrictUpdating = true;
    }

    public void allow() {
        restrictUpdating = false;
    }

    public boolean isUpdatesRestricted() {
        return restrictUpdating;
    }
}

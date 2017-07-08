package org.concordion.api.listener;

import org.concordion.api.Element;

public abstract class AbstractElementEvent {

  private final Element element;

  public AbstractElementEvent(Element element) {
    this.element = element;
  }

  public Element getElement() {
    return element;
  }
}

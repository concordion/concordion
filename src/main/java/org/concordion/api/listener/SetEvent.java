package org.concordion.api.listener;

import org.concordion.api.Element;

public class SetEvent extends AbstractElementEvent {

  private String expression;

  public SetEvent(Element element, String expression) {
    super(element);
    this.expression = expression;
  }

  public String getExpression() {
    return expression;
  }
}

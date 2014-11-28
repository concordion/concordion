package org.concordion.internal;

import org.concordion.api.Element;

public class ListEntry {
	
	private final Element listElement;
	
	public ListEntry(Element listElement) {
		this.listElement = listElement;
	}
	
	public Element getElement() {
		return listElement;
	}

	public boolean isItem() {
		return getElement().isNamed("li");
	}
	
	public boolean isList() {
		return getElement().isNamed("ul") || getElement().isNamed("ol");
	}

}

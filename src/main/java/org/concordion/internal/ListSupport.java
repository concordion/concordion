package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;

public class ListSupport {

	private final CommandCall listCommandCall;

	public ListSupport(CommandCall listCommandCall) {
		assert (listCommandCall.getElement().isNamed("ol") || listCommandCall.getElement().isNamed("ul"));

        this.listCommandCall = listCommandCall;
	}
	
	public List<ListEntry> getListEntries() {
		List<ListEntry> listEntries = new ArrayList<ListEntry>();
		for (int i = 0; i < listCommandCall.getElement().getChildElements().length; i++) {
			Element listElement = listCommandCall.getElement().getChildElements()[i];
			if (listElement.isNamed("li") ||
                listElement.isNamed("ul") ||
                listElement.isNamed("ol")) {
				listEntries.add(new ListEntry(listElement));
			}
		}
		return listEntries;
	}

}

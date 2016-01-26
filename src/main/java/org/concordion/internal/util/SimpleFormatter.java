package org.concordion.internal.util;

import java.util.Arrays;
import java.util.List;

public class SimpleFormatter {
	
	public static String format(String format, Object... args) {
		StringBuilder result = new StringBuilder();
		String[] stringElements = format.split("%s");
		if (format.endsWith("%s")) {
			List<String> stringElementsList = Arrays.asList(stringElements);
			stringElements = stringElementsList.toArray(new String[stringElements.length + 1]);
			stringElements[stringElements.length - 1] = "";
		}
		result.append(stringElements[0]);
		for (int i = 1; i < stringElements.length; i++) {
			if (args != null && args.length >= i && args[i-1] != null) {
				result.append(args[i-1].toString());
			}
			result.append(stringElements[i]);
		}
		return result.toString();
	}

}

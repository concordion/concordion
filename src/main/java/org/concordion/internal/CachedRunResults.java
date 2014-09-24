package org.concordion.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.concordion.api.ResultSummary;

public class CachedRunResults {

	Map<Class<?>, ResultSummary> map = new ConcurrentHashMap<>();

	public ResultSummary getCachedResults(Class<?> class1) {
		return map.get(class1);
	}

	public void addResultSummary(Class<?> class1,
			ResultSummary resultSummary) {
		map.put(class1, resultSummary);
	}

}

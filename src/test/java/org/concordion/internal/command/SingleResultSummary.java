package org.concordion.internal.command;

import org.concordion.api.Result;
import org.concordion.internal.SummarizingResultRecorder;

public class SingleResultSummary extends SummarizingResultRecorder {
	private final Result result;

	public SingleResultSummary(final Result result) {
		this.record(result);
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SingleResultSummary) {
			return this.result == ((SingleResultSummary)o).getResult();
		}
		return false;
	}
}

package org.concordion.internal.command;

import org.concordion.api.Result;
import org.concordion.internal.SummarizingResultRecorder;

public class SingleResultSummary extends SummarizingResultRecorder {
	public SingleResultSummary(final Result result) {
		this.record(result);
	}
}

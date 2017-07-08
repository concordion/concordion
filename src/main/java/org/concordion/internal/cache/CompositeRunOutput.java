package org.concordion.internal.cache;

import org.concordion.api.ResultSummary;
import org.concordion.internal.RunOutput;
import org.concordion.internal.SummarizingResultRecorder;

import java.util.ArrayList;
import java.util.List;

class CompositeRunOutput implements RunOutput {
    private List<RunOutput> results = new ArrayList<RunOutput>();
    private String specificationDescription;

    public CompositeRunOutput(String specificationDescription) {
        super();
        this.specificationDescription = specificationDescription;
    }

    public void add(RunOutput exampleRunOutput) {
        results.add(exampleRunOutput);
    }

    @Override
    public ResultSummary getActualResultSummary() {
        SummarizingResultRecorder totalResultSummary = new SummarizingResultRecorder(specificationDescription);
        for (RunOutput result : results) {
            totalResultSummary.record(result.getActualResultSummary());
        }
        return totalResultSummary;
    }

    @Override
    public ResultSummary getModifiedResultSummary() {
        SummarizingResultRecorder totalResultSummary = new SummarizingResultRecorder(specificationDescription);
        for (RunOutput result : results) {
            totalResultSummary.record(result.getModifiedResultSummary());
        }
        return totalResultSummary;
    }
}

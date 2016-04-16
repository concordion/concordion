package org.concordion.internal.cache;

import org.concordion.api.ResultSummary;
import org.concordion.internal.SummarizingResultRecorder;

import java.util.ArrayList;
import java.util.List;

public class CompositeRunOutput extends ConcordionRunOutput {
    private List<ConcordionRunOutput> results = new ArrayList<ConcordionRunOutput>();
    private String specificationDescription;

    public CompositeRunOutput(String specificationDescription) {
        super(null);
        this.specificationDescription = specificationDescription;
    }

    public void add(ConcordionRunOutput exampleRunOutput) {
        results.add(exampleRunOutput);
    }

    @Override
    public ResultSummary getActualResultSummary() {
        SummarizingResultRecorder totalResultSummary = new SummarizingResultRecorder(specificationDescription);
        for (ConcordionRunOutput result : results) {
            totalResultSummary.record(result.getActualResultSummary());
        }
        return totalResultSummary;
    }

    @Override
    public ResultSummary getModifiedResultSummary() {
        SummarizingResultRecorder totalResultSummary = new SummarizingResultRecorder(specificationDescription);
        for (ConcordionRunOutput result : results) {
            totalResultSummary.record(result.getModifiedResultSummary());
        }
        return totalResultSummary;
    }
}

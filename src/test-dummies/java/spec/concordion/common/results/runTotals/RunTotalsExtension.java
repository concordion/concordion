package spec.concordion.common.results.runTotals;

import org.concordion.api.Element;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.*;
import org.concordion.internal.FixtureInstance;
import org.concordion.internal.FixtureType;

public class RunTotalsExtension implements ConcordionExtension, RunListener {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withRunListener(this);
    }

    public void throwableCaught(ThrowableCaughtEvent event) {
        writeText(event.getElement(), "threw exception");
    }

    @Override
    public void runStarted(RunStartedEvent runStartedEvent) {
    }

    @Override
    public void successReported(RunSuccessEvent event) {
        writeText(event);
    }

    @Override
    public void failureReported(RunFailureEvent event) {
        writeText(event);
    }

    @Override
    public void ignoredReported(RunIgnoreEvent event) {
        writeText(event);
    }

    private void writeText(AbstractRunEvent event) {
        writeText(event.getElement(), event.getResultSummary().printCountsToString(new FixtureType(Object.class)));
    }

    private void writeText(Element element, String text) {
        Element sister = new Element("counts");
        sister.appendText(" (" + text + ")");
        element.appendSister(sister);
    }
}

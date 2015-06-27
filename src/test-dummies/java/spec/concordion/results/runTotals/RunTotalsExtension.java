package spec.concordion.results.runTotals;

import org.concordion.api.Element;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.*;

/**
 * Created by tim on 25/06/15.
 */
public class RunTotalsExtension implements ConcordionExtension, RunListener {

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withRunListener(this);
    }

    @Override
    public void throwableCaught(ThrowableCaughtEvent event) {
        writeText(event.getElement(), "threw exception");
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
        writeText(event.getElement(), event.getResultSummary().printCountsToString(event.getResultSummary()));
    }

    private void writeText(Element element, String text) {
        Element sister = new Element("counts");
        sister.appendText(" (" + text + ")");
        element.appendSister(sister);
    }

}

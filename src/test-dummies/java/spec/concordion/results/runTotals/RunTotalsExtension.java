package spec.concordion.results.runTotals;

import org.concordion.api.Element;
import org.concordion.api.Fixture;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.AbstractRunEvent;
import org.concordion.api.listener.RunFailureEvent;
import org.concordion.api.listener.RunIgnoreEvent;
import org.concordion.api.listener.RunListener;
import org.concordion.api.listener.RunSuccessEvent;
import org.concordion.api.listener.ThrowableCaughtEvent;

/**
 * Created by tim on 25/06/15.
 */
public class RunTotalsExtension implements ConcordionExtension, RunListener {

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withRunListener(this);
    }

    public void throwableCaught(ThrowableCaughtEvent event) {
        writeText(event.getElement(), "threw exception");
    }


    public void successReported(RunSuccessEvent event) {
        writeText(event);
    }


    public void failureReported(RunFailureEvent event) {
        writeText(event);
    }

    public void ignoredReported(RunIgnoreEvent event) {
        writeText(event);
    }

    private void writeText(AbstractRunEvent event) {
        writeText(event.getElement(), event.getResultSummary().printCountsToString(new Fixture(new Object())));
    }

    private void writeText(Element element, String text) {
        Element sister = new Element("counts");
        sister.appendText(" (" + text + ")");
        element.appendSister(sister);
    }

}

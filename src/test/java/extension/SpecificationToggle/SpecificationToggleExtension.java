package extension.SpecificationToggle;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class SpecificationToggleExtension implements ConcordionExtension {

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withLinkedCSS("/toggle_html.css", new Resource("/toggle_html.css"));
        concordionExtender.withLinkedJavaScript("/toggle_html.js", new Resource("/toggle_html.js"));
    }

}

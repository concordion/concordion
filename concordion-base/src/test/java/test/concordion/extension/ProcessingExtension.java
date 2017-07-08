package test.concordion.extension;

import java.io.PrintStream;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class ProcessingExtension implements ConcordionExtension {

    private AssertLogger assertLogger = new AssertLogger();
    private ConcordionBuildLogger concordionBuildLogger = new ConcordionBuildLogger();
    private SpecificationProcessingLogger specificationProcessingLogger = new SpecificationProcessingLogger();
    private DocumentParsingLogger documentParsingLogger = new DocumentParsingLogger();

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withAssertEqualsListener(assertLogger);
        concordionExtender.withBuildListener(concordionBuildLogger);
        concordionExtender.withSpecificationProcessingListener(specificationProcessingLogger);
        concordionExtender.withDocumentParsingListener(documentParsingLogger);
    }
    
    public ProcessingExtension withStream(PrintStream stream) {
        assertLogger.setStream(stream);
        concordionBuildLogger.setStream(stream);
        specificationProcessingLogger.setStream(stream);
        documentParsingLogger.setStream(stream);
        return this;
    }
}

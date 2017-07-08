package test.concordion.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class DiagnosticCollector implements DiagnosticListener<JavaFileObject> {

    private final List<DiagnosticMessage> diagnosticMessages = new ArrayList<DiagnosticMessage>();
    private Locale locale = Locale.getDefault();

    public void report(final Diagnostic<? extends JavaFileObject> diagnostic) {
        diagnosticMessages.add(new DiagnosticMessage() {

            public String getMessage() {
                return diagnostic.getMessage(locale);
            }
        });
    }

    public DiagnosticMessage[] getDiagnosticMessages() {
        return diagnosticMessages.toArray(new DiagnosticMessage[0]);
    }
}

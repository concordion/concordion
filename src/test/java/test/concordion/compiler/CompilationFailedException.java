package test.concordion.compiler;

import java.io.PrintStream;

public class CompilationFailedException extends Exception {

    private static final long serialVersionUID = 8509895786810023674L;
    
    private final DiagnosticMessage[] diagnostics;

    public CompilationFailedException(String message, DiagnosticMessage ... diagnostics) {
        super(message);
        this.diagnostics = diagnostics;
    }
    
    public DiagnosticMessage[] getDiagnosticMessages() {
        return diagnostics;
    }

    public void printDiagnosticsTo(PrintStream out) {
        for (DiagnosticMessage m : diagnostics) {
            out.println(m.getMessage());
        }
    }
}


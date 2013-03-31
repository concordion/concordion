package test.concordion.compiler;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavaCompiler implements Compiler {

    private final javax.tools.JavaCompiler systemJavaCompiler;
    private Locale locale = Locale.getDefault();
    private Charset charset = Charset.forName("UTF-8");
    private DiagnosticCollector diagnosticCollector;
    private SimulatedFileManager simulatedFileManager;
    private SimulatedClassLoader simulatedClassLoader;

    public JavaCompiler() {
        systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
        if (systemJavaCompiler == null) {
            throw new CompilerNotAvailableException("Java compiler not available. Requires JDK1.6 or later (not a JRE)");
        }
        diagnosticCollector = new DiagnosticCollector();
        simulatedFileManager = new SimulatedFileManager(getStandardFileManager(diagnosticCollector));
        simulatedClassLoader = new SimulatedClassLoader(simulatedFileManager);
    }

    public void compile(Source... sources) throws CompilationFailedException {
        Writer out = null;
        String classPath = System.getProperty("java.class.path");
        Iterable<String> options = Arrays.asList(new String[] { "-classpath", classPath });
        Iterable<String> classes = null;
        
        List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
        for (Source source : sources) {
            compilationUnits.add(new SimulatedJavaSourceFile(source));
        }
        
        Boolean success = systemJavaCompiler.getTask(out, simulatedFileManager, diagnosticCollector, options, classes, compilationUnits).call();
        
        if (!success) {
            throw new CompilationFailedException("Compilation failed", diagnosticCollector.getDiagnosticMessages());
        }
    }

    private StandardJavaFileManager getStandardFileManager(DiagnosticListener<JavaFileObject> diagnosticListener) {
        return systemJavaCompiler.getStandardFileManager(diagnosticListener, locale, charset);
    }
    
    public Class<?> forName(String name) throws ClassNotFoundException {
        boolean initialize = false;
        return Class.forName(name, initialize, simulatedClassLoader);
    }
}


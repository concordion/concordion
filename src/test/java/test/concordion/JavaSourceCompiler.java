package test.concordion;

import test.concordion.compiler.CompilationFailedException;
import test.concordion.compiler.Source;

public class JavaSourceCompiler {

    private test.concordion.compiler.JavaCompiler compiler = new test.concordion.compiler.JavaCompiler();
    
    public Class<?> compile(String className, String sourceCode) throws Exception {
        
        try {
            compiler.compile(new Source(sourceCode, className + ".java"));
        } catch (CompilationFailedException e) {
            e.printDiagnosticsTo(System.out);
        }
        
        return compiler.forName(className);
    }
}

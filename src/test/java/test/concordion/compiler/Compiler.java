package test.concordion.compiler;

public interface Compiler {

    void compile(Source ... source) throws CompilationFailedException;
}


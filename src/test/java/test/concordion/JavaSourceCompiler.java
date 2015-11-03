package test.concordion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.concordion.compiler.CompilationFailedException;
import test.concordion.compiler.Source;

public class JavaSourceCompiler {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("class\\s*(.*?)\\s*(\\{|extends)");
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("(?m)^package\\s*(.*);$");
    private test.concordion.compiler.JavaCompiler compiler = new test.concordion.compiler.JavaCompiler();

    public Class<?> compile(String sourceCode) throws Exception {
        String qualifiedClassName = getPackageName(sourceCode) + getClassName(sourceCode);
        return compile(qualifiedClassName, sourceCode);
    }

    public Class<?> compile(String className, String sourceCode) throws Exception {
        
        try {
            compiler.compile(new Source(sourceCode, className.replaceAll("\\.", "/") + ".java"));
        } catch (CompilationFailedException e) {
            e.printDiagnosticsTo(System.out);
        }
        
        return compiler.forName(className);
    }
    
    public Class<?> compileWithParent(String sourceCode, String sourceCodeParent) throws Exception {
        String qualifiedClassName = getPackageName(sourceCode) + getClassName(sourceCode);
        String qualifiedParentClassName = getPackageName(sourceCodeParent) + getClassName(sourceCodeParent);
        return compile(qualifiedClassName, sourceCode, qualifiedParentClassName, sourceCodeParent);
    }

    public Class<?> compile(String className, String sourceCode, String classNameParent, String sourceCodeParent) throws Exception {
    	try {
            compiler.compile(new Source(sourceCode, className + ".java"), new Source(sourceCodeParent, classNameParent + ".java"));
        } catch (CompilationFailedException e) {
            e.printDiagnosticsTo(System.out);
        }
        
        return compiler.forName(className);
    }

    private String getClassName(String javaFragment) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(javaFragment);
        matcher.find();

        return matcher.group(1);
    }

    private String getPackageName(String javaFragment) {
        Matcher matcher = PACKAGE_NAME_PATTERN.matcher(javaFragment);
        if (!matcher.find()) {
            return "";
        }

        String packageName = matcher.group(1);

        if (!packageName.isEmpty()) {
            packageName += ".";
        }

        return packageName;
    }
}

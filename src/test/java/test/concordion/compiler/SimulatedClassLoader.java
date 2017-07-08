package test.concordion.compiler;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.concordion.internal.util.Check;

public class SimulatedClassLoader extends ClassLoader {

    private final SimulatedFileManager simulatedFileManager;

    public SimulatedClassLoader(SimulatedFileManager fileManager) {
        this.simulatedFileManager = fileManager;
    }

    @Override
    protected Class<?> findClass(final String className) throws ClassNotFoundException {
        Location location = null;            
        FileObject sibling = null;
        JavaFileObject javaFileObject;
        try {
            javaFileObject = simulatedFileManager.getJavaFileForOutput(location, className, Kind.CLASS, sibling);
        } catch (IOException e) {
            throw new IllegalStateException("SimulatedFileManager should return null, rather than throw an exception", e);
        }
        if (javaFileObject == null) {
            return super.findClass(className);
        }
        
        Check.isTrue(javaFileObject instanceof SimulatedJavaClassFile, "Unexpected JavaFileObject type");
        
        SimulatedJavaClassFile file = (SimulatedJavaClassFile) javaFileObject;
        if (file.getBytes().length == 0) {
            throw new ClassNotFoundException("Class '" + className + "' not found");
        }
        return defineClass(className, file.getBytes(), 0, file.getBytes().length);
    }
}

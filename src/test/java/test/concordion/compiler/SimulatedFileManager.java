package test.concordion.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

class SimulatedFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, SimulatedJavaClassFile> classFileMap = new HashMap<String, SimulatedJavaClassFile>();
    
    public SimulatedFileManager(JavaFileManager wrappedFileManager) {
        super(wrappedFileManager);
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
            throws IOException {
        
        SimulatedJavaClassFile classFile = classFileMap.get(className);
        if (classFile == null) {
            classFile = new SimulatedJavaClassFile(Source.toURI(className), kind);
            classFileMap.put(className, classFile);
        }
        
        return classFile;
    }
    
    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof SimulatedJavaClassFile) {
            return file.getName();
        }
        return super.inferBinaryName(location, file);
    }
    
    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
            throws IOException {
        
        List<JavaFileObject> list = new ArrayList<JavaFileObject>();
        for (JavaFileObject obj : super.list(location, packageName, kinds, recurse)) {
            list.add(obj);
        }
        
        if ((location == StandardLocation.CLASS_PATH) && kinds.contains(Kind.CLASS)) {
            list.addAll(classFileMap.values());
        }
        return list;
    }
}

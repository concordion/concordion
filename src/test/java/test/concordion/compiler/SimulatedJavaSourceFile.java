package test.concordion.compiler;

import java.io.IOException;

import javax.tools.SimpleJavaFileObject;

public class SimulatedJavaSourceFile extends SimpleJavaFileObject {

    private final String content;

    SimulatedJavaSourceFile(Source source) {
        super(source.getPath(), Kind.SOURCE);
        this.content = source.getContent();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException, IllegalStateException,
            UnsupportedOperationException {
        return content;
    }
}

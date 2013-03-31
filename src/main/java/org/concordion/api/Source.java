package org.concordion.api;

import java.io.IOException;
import java.io.InputStream;

public interface Source {

    InputStream createInputStream(Resource resource) throws IOException;
    
    boolean canFind(Resource resource);
}

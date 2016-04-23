/*
 * Copyright (c) 2010 Two Ten Consulting Limited, New Zealand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.concordion;

import org.concordion.api.Resource;
import org.concordion.api.Source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilterSource implements Source {

    private final Source source;
    private String prefix;

    public FilterSource(Source source, String filterPrefix) {
        this.source = source;
        prefix = filterPrefix;
    }

    public InputStream createInputStream(Resource resource) throws IOException {
        if (resource.getPath().startsWith(prefix)) {
            return new ByteArrayInputStream("x".getBytes("UTF-8"));
        }
        return source.createInputStream(resource);
    }

    public boolean canFind(Resource resource) {
        return true;
    }

    @Override
    public String readAsString(InputStream inputStream) throws IOException {
        return null;
    }

    @Override
    public String readResourceAsString(String resourcePath) {
        return null;
    }
}

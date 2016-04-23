package org.concordion.api;

import nu.xom.Builder;
import nu.xom.Document;
import org.concordion.internal.XMLParser;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

public class ScopedObjectHolderTest {

    ScopedObjectHolder<Integer> holder = new ScopedObjectHolder<Integer>() {
        @Override
        protected Integer create() {
            return 1;
        }
    };

    @Test
    public void isNotCreatedInitially() {
        assertFalse("expected holder to not be created yet", holder.isCreated());
    }

    @Test
    public void isCreatedAfterGet() {
        holder.get();
        assertTrue("expected holder be created after get()", holder.isCreated());
    }

    @Test
    public void returnsCurrentValueAfterGet() {
        Integer expected = 1;
        Integer actual = holder.get();
        assertEquals(expected, actual);
    }
}

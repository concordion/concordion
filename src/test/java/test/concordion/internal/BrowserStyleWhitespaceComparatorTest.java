package test.concordion.internal;

import junit.framework.Assert;
import org.concordion.internal.BrowserStyleWhitespaceComparator;
import org.junit.Test;

public class BrowserStyleWhitespaceComparatorTest {
    private BrowserStyleWhitespaceComparator comparator = new BrowserStyleWhitespaceComparator();

    @Test
    public void normalizesItemsBeforeComparing() {
        assertNotConsideredEqual("abc", "def");
        assertNotConsideredEqual("abc", "ABC");
        assertNotConsideredEqual("abc", "Abc");
        assertNotConsideredEqual("abc", "abc.");
        
        assertConsideredEqual("abc", "abc");
        assertConsideredEqual("a bc", "a    bc");
        assertConsideredEqual("   a bc ", "a    bc");
        assertConsideredEqual("a bc", "a    bc ");
        assertConsideredEqual("a bc", "  a    bc \n  ");
    }
    
    @Test
    public void considersAStringRepresentationOfNullAsEqualToNull() {
        assertConsideredEqual(null, "(null)");
    }
    
    @Test
    public void acceptsVariousFormsOfBooleanString() {
        assertConsideredEqual(false, "false");
        assertConsideredEqual(true, "true");
        assertConsideredEqual(true, "Yes");
        assertConsideredEqual(true, "y");
        assertConsideredEqual(true, " y ");
        assertNotConsideredEqual(true, "false");
        assertNotConsideredEqual(false, "true");
        assertNotConsideredEqual(false, "True");
        assertNotConsideredEqual(false, "Y");
        assertNotConsideredEqual(true, "N");
        assertNotConsideredEqual(true, "Aye");
    }
    
    private void assertConsideredEqual(Object obj1, Object obj2) {
        Assert.assertEquals(0, comparator.compare(obj1, obj2));
    }
    
    private void assertNotConsideredEqual(Object obj1, Object obj2) {
        Assert.assertFalse(comparator.compare(obj1, obj2) == 0);
    }
}


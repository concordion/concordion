package test.concordion.api;

import org.concordion.api.Resource;

import junit.framework.TestCase;

public class ResourceTest extends TestCase {

    public void testCanTellYouItsParent() throws Exception {
        assertEquals("/", parentPathOf("/abc"));
        assertEquals("/", parentPathOf("/abc/"));
        assertEquals("/abc/", parentPathOf("/abc/def"));
        assertEquals("/abc/", parentPathOf("/abc/def/"));
        assertEquals("/abc/def/", parentPathOf("/abc/def/ghi"));
    }
    
    public void testReturnsNullForParentOfRoot() {
        assertNull(new Resource("/").getParent());
    }

    public void testCanCalculateRelativePaths() throws Exception {
        assertEquals("x.html", relativePath("/spec/x.html", "/spec/x.html"));
        assertEquals("blah", relativePath("/spec/", "/spec/blah"));
        assertEquals("../x/", relativePath("/a/b/c/", "/a/b/x/"));
        assertEquals("../../../a/b/x/", relativePath("/x/b/c/", "/a/b/x/"));
        assertEquals("../../x/x/file.txt", relativePath("/a/b/c/file.txt", "/a/x/x/file.txt"));
        assertEquals("../file.txt", relativePath("/a/file.txt", "/file.txt"));
        assertEquals("../../../file.txt", relativePath("/a/b/c/file.txt", "/file.txt"));
        assertEquals("../../../image/concordion-logo.png", relativePath("/spec/concordion/breadcrumbs/Breadcrumbs.html", "/image/concordion-logo.png"));
    }

    public void testGivenRelativePathFromOneResourceReturnsOtherResource() {
        assertEquals("/david.html", getResourceRelativeTo("/blah.html", "david.html"));
        assertEquals("/david.html", getResourceRelativeTo("/", "david.html"));
        assertEquals("/blah/david.html", getResourceRelativeTo("/blah/x", "david.html"));
        assertEquals("/blah/x/david.html", getResourceRelativeTo("/blah/x/y", "david.html"));
        assertEquals("/blah/x/z/david.html", getResourceRelativeTo("/blah/x/y", "z/david.html"));
        assertEquals("/blah/style.css", getResourceRelativeTo("/blah/docs/example.html", "../style.css"));
        assertEquals("/style.css", getResourceRelativeTo("/blah/docs/example.html", "../../style.css"));
        assertEquals("/blah/style.css", getResourceRelativeTo("/blah/docs/work/example.html", "../../style.css"));
        assertEquals("/blah/docs/style.css", getResourceRelativeTo("/blah/docs/work/example.html", "../style.css"));
        assertEquals("/style.css", getResourceRelativeTo("/blah/example.html", "../style.css"));
        assertEquals("/style.css", getResourceRelativeTo("/blah/", "../style.css"));
        assertEquals("/style.css", getResourceRelativeTo("/blah", "style.css"));
        assertEquals("/blah/docs/css/style.css", getResourceRelativeTo("/blah/docs/work/", "../css/style.css"));
    }

    public void testThrowsExceptionIfRelativePathPointsAboveRoot() {
        try {
            getResourceRelativeTo("/blah/docs/example.html", "../../../style.css");
            fail();
        } catch (Exception e) {
            assertEquals("Path '../../../style.css' relative to '/blah/docs/example.html' " +
                    "evaluates above the root package.", e.getMessage());
        }
    }
    
    private String getResourceRelativeTo(String resourcePath, String relativePath) {
        return new Resource(resourcePath).getRelativeResource(relativePath).getPath();
    }

    
    private String relativePath(String from, String to) {
        return resource(from).getRelativePath(resource(to));
    }

    private String parentPathOf(String path) {
        return resource(path).getParent().getPath();
    }
    
    private Resource resource(String path) {
        return new Resource(path);
    }
}

package org.concordion.internal.runner;

/**
 * Created by tim on 23/06/15.
 */
public class AnnotatedClass extends AnnotatedClassBase {

    @TestAnnotation
    public String publicString = "";

    @TestAnnotation
    protected String protectedString = "";

    @TestAnnotation
    private String privateString = "";

    @TestAnnotation
    String packageString = "";

    public String unannotatedpublicString = "";
    protected String unannotatedprotectedString = "";
    private String unannotatedprivateString = "";
    String unannotatedpackageString = "";


    @TestAnnotation
    public void annotatedMethodPublic() {

    }
    @TestAnnotation
    private void annotatedMethodPrivate() {

    }
    @TestAnnotation
    protected void annotatedMethodProtected() {

    }
    @TestAnnotation
    void annotatedMethodPackage() {

    }
    public void unannotatedMethodPublic() {

    }
    private void unannotatedMethodPrivate() {

    }
    protected void unannotatedMethodProtected() {

    }
    void unannotatedMethodPackage() {

    }

}

package org.concordion.internal.runner;

/**
 * Created by tim on 23/06/15.
 */
public class AnnotatedClassBase {

    @TestAnnotation
    public String publicStringBase = "";

    @TestAnnotation
    protected String protectedStringBase = "";

    @TestAnnotation
    private String privateStringBase = "";

    @TestAnnotation
    String packageStringBase = "";


    public String unannotatedpublicStringBase = "";
    protected String unannotatedprotectedStringBase = "";
    private String unannotatedprivateStringBase = "";
    String unannotatedpackageStringBase = "";


    @TestAnnotation
    public void annotatedMethodPublicBase() {

    }
    @TestAnnotation
    private void annotatedMethodPrivateBase() {

    }
    @TestAnnotation
    protected void annotatedMethodProtectedBase() {

    }
    @TestAnnotation
    void annotatedMethodPackageBase() {

    }
    public void unannotatedMethodPublicBase() {

    }
    private void unannotatedMethodPrivateBase() {

    }
    protected void unannotatedMethodProtectedBase() {

    }
    void unannotatedMethodPackageBase() {

    }

}

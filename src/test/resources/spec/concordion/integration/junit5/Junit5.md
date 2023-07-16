# JUnit 5 Jupiter
_Since_: Concordion 4.0.0

An active specification with JUnit 5 Jupiter consists of two parts: a specification (eg. in HTML, Markdown or Excel)
and a Java fixture annotated with `@ConcordionFixture`.

The only other difference from JUnit 4 (and JUnit 5 Vintage) is that the JUnit annotations are no longer supported 
with JUnit 5 Jupiter.

### Fixture naming

The fixture class must be in the same package as the specification.
The fixture class must have the same name as the specification (excluding the file extension), and can optionally have
`Test` or `Fixture` appended.

When you run the fixture class using JUnit, it will automatically locate the specification file. 
It does this by looking in the same package for the specification file named the same as the test, 
but without the `Test` or `Fixture` suffixes.

#### Example

If your test is called `com.example.BreadcrumbTest` then the corresponding
specification file must be in the same package (`com.example`) and called
`Breadcrumb.html`. (Other specification types will use different suffices, eg. `.md` or `.markdown` for Markdown specifications.)

### Reverse lookup

When using the [run command](../../common/command/run/Run.html "c:run"), the fixture class is located from the referenced specification by
looking in the same package as the specification for a JUnit test named the same as the specification with optionally `Test` or
`Fixture` appended.

#### Example

If your specification contains a run command referencing the specification `Breadcrumb.md` in the `com.example` package, 
Concordion will look in the following order for a corresponding fixture:

1. `com.example.Breadcrumb`
2. `com.example.BreadcrumbTest`
3. `com.example.BreadcrumbFixture`

### JUnit Annotations

Are not supported with JUnit 5 Jupiter.

Instead the [Concordion annotations](../../annotation/Annotation.md "c:run") are available and will work with all 
supported versions of JUnit.

### Fixture Example

    import org.concordion.api.ConcordionFixture;
    
    @ConcordionFixture
    public class JUnit5Test {

    // ...
    }

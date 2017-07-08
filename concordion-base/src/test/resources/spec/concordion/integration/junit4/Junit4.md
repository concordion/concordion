# JUnit 4

An active specification with JUnit 4 consists of two parts: a specification (eg. in HTML, Markdown or Excel)
and a Java fixture annotated with `@RunWith(ConcordionRunner.class)`.

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

When using the [run command](../../command/run/Run.html "c:run"), the fixture class is located from the referenced specification by
looking in the same package as the specification for a JUnit test named the same as the specification with optionally `Test` or
`Fixture` appended.

#### Example

If your specification contains a run command referencing the specification `Breadcrumb.md` in the `com.example` package, 
Concordion will look in the following order for a corresponding fixture:

1. `com.example.Breadcrumb`
2. `com.example.BreadcrumbTest`
3. `com.example.BreadcrumbFixture`

#### Non-Concordion fixture exammple

Running the specification [Foo](Foo.md "c:run") which has (non-Concordion) JUnit classes named `Foo.java`, `FooTest.java` 
and a Concordion fixture named `FooFixture.java` in the same package will ignore the JUnit classes when locating
 the fixture and will run [FooFixture](- "?=getFooFixtureClass()").

### JUnit Annotations

The following JUnit annotations can be used. However, the [Concordion annotations](../../annotation/Annotation.md "c:run") are 
 preferred, and support additional features.

#### [Supported JUnit annotations](- "annotations")
The JUnit 4 integration supports the [Before](- "c:assertTrue=wasBeforeCalled()"),
[BeforeClass](- "c:assertTrue=wasBeforeClassCalled()"), and [Rule](- "c:assertTrue=wasRuleInvoked()") annotations.

### Fixture Example

    import org.concordion.integration.junit4.ConcordionRunner;
    import org.junit.runner.RunWith;
    
    @RunWith(ConcordionRunner.class)
    public class JUnit4Test {

    // ...
    }

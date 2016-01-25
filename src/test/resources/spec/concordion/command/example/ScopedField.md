# Field Scope in Fixtures
_Since_: Concordion 2.0.0

Concordion encourages you to keep your [examples](Examples.html) completely independent of each other. 
This allows individual examples to be run in isolation. It also makes the specification easier to follow when you can read examples in isolation.

To support this behaviour, Concordion reinitialises the fields in fixture objects for each Concordion example (where the example is using the `concordion:example` command). This is standard JUnit behaviour (in fact, JUnit creates a new fixture object for each test).

However, we recognise that sometimes you will want to share fields across a specification when the field is expensive to initialise, for example a browser instance or database connection. Concordion provides support for specification scoped instance fields, in addition to the default example scope.

Scoped fields must be wrapped in [ScopedObjectHolder](http://concordion.github.io/concordion/latest/javadoc/org/concordion/api/ScopedObjectHolder.html) and annotated
with [@ConcordionScoped](http://concordion.github.io/concordion/latest/javadoc/org/concordion/api/ConcordionScoped.html).

For example:

    @ConcordionScoped(Scope.SPECIFICATION)
    private ScopedObjectHolder<AtomicInteger> specScopedCounter = new ScopedObjectHolder<AtomicInteger>() {
        @Override
        protected AtomicInteger create() {
            return new AtomicInteger();
        }
    };


The scoped object is created lazily when you call the `get()` method on the `ScopedObjectHolder`. `ScopedObjectHolder` also provides a `destroy()` method that you can override to clean-up resources when they go out of scope.

We have two examples below. Each example increments an AtomicInteger that is scoped differently and echoes the value.

## [Before each example](- "before")

* [Increment all counters](- "incrementAllCounters()")

## [Test that increments counters](- "test1")

* Unannotated field value is [1](- "?=getFieldCounter()")
* Example scope value is [1](- "?=getExampleScopedCounter()")
* Specification scope value is [1](- "?=getSpecScopedCounter()")

## [The same test again](- "test2")

* Unannotated field value is [1](- "?=getFieldCounter()")
* Example scope value is [1](- "?=getExampleScopedCounter()")
* Specification scope value is [2](- "?=getSpecScopedCounter()")
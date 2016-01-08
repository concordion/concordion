[] Concordion - remove Fixture from constructor?
[] SpecificationLocator - constructs Fixture
[] Improve console output to clearly show Example, Outer and Summary 
[] Revisit the example totals count algorithm
[] Change the "before" tag in examples to "beforeEach". This is more descriptive and will open the way for a "beforeAll" at a later stage. If doing this, I would deprecate "before" in the upcoming snapshot and add "beforeEach", then remove "before" in the following snapshot
[] Allow XHTML file extensions
[] Multiple fixtures created for non-example specs
[] Only run outer example if it has commands in it
[] If Outer example has no commands, don't print Outer successes and failures to console
[] When using concordion:run, the specs should show as nested JUnit results?
[] Move Before and After Suite and Spec code out from JUnit4 code
[] Totals not aggregated to index page when using parallel runner
[] copyValueFromField is only called from beforeSpecification, so how does the code in getObject() that creates a new object per example get called for each example?
[] Exceptions in before examples are not reported on command line
[] Ensure spec scoped variables destroyed on exception
[] If Scoped variable is defined, but no annotation added, what to do?

Created lazily, is that OK? Maybe not thread-safe?
Allow scope to be easily changed - to allow easy migration to an example-based runner
Allow for future extensibility with tags to define when hook is run (similar to Cucumber rules)

Don't allow parallel examples, since XOM is not thread-safe - eg. Screenshot extension is updating output spec
- [ ] Only run outer example if it has commands in it
- [ ] If Outer example has no commands, don't print Outer successes and failures to console
- [ ] Improve console output to clearly show Example, Outer and Summary 
- [ ] When using concordion:run, the specs should show as nested JUnit results?
- [ ] Revisit the example totals count algorithm
- [ ] ? Change the "before" tag in examples to "beforeEach". This is more descriptive and will open the way for a "beforeAll" at a later stage. If doing this, I would deprecate "before" in the upcoming snapshot and add "beforeEach", then remove "before" in the following snapshot
- [ ] Totals not aggregated to index page when using parallel runner
- [ ] Exceptions in before examples are not reported on command line
- [ ] Ensure spec and suite scoped variables destroyed on exception
- [ ] If Scoped variable is defined, but no annotation added, what to do?
- [ ] Check what's in API and what's in internal
- [ ] Should Before and After annotated methods be called from existing listeners?
- [ ] Optimisation - create maps of annotated fields and methods so we don't have to iterate across classes each time
- [ ] Change from AnnotationFormatError
- [ ] Change so that protected or public methods with annotations are accepted 

__Document__ 
- [ ] Update javadocs, remove created by from docs
- [ ] Don't allow parallel examples, since XOM is not thread-safe - eg. Screenshot extension is updating output spec
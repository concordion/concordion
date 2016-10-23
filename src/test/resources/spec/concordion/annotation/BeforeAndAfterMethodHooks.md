# Before and After Hooks
_Since_: Concordion 2.0.0

Concordion provides hooks to invoke fixture methods before or after an __example__, __specification__ or __suite__.

## Example hooks 

Annotate a fixture method with `@BeforeExample` to invoke it before each example, or `@AfterExample` to invoke it after each example.

Optionally, the method can have a String parameter annotated with `@ExampleName`, which will be supplied with the name of the example. For example: 

    @BeforeExample
    public void setupExample(@ExampleName exampleName) {
    }    

## Specification hooks 

Annotate a fixture method with `@BeforeSpecification` to invoke it before any of the examples are executed, or `@AfterSpecification` to invoke it after all the examples are executed.

## Suite hooks 

Annotate a fixture method with `@BeforeSuite` to invoke it before any of the specifications run, or `@AfterSuite` to invoke it after all the specifications run.

Note that for the suite hooks to be run, they must exist on the fixture that is being executed directly by the test runner (eg. by JUnit).

## [Example] (- "example-hooks")

Running the specification [BeforeAndAfterLogging](BeforeAndAfterLogging.md "c:run") which logs messages in its `@BeforeSpecification`, `@BeforeExample`, `@AfterExample` and `@AfterSpecification` methods, as well as in the specification and examples, results in the following : 

|[checkRows][] [text][]|
|----------------------|
|Before specification  |
|Before [Outer]        |
|In specification      |
|After [Outer]         |
|Before example1       |
|In example 1          |
|After example1        |
|Before example2       |
|In example 2          |
|After example2        |
|After specification   |
 

[checkRows]: - "c:verifyRows=#line : getLog()"
[text]:      - "?=#line"


## [Example With Listener] (- "example-listener-hooks")

Running the specification [BeforeAndAfterLoggingWithListener](BeforeAndAfterLoggingWithListener.md "c:run") which in addition logging the before and after methods, also has two extensions which listen out for the same events and logs those, results in the following : 

|[checkRows2][] [text][]|
|----------------------|
|Extension 1: beforeProcessingSpecification |
|Extension 2: beforeProcessingSpecification |
|Before specification  |
|Extension 1: Before outer example [Outer]  |
|Extension 2: Before outer example [Outer]  |
|Before [Outer]        |
|In specification      |
|After [Outer]         |
|Extension 2: After outer example [Outer]  |
|Extension 1: After outer example [Outer]  |
|Extension 1: Before example example1 |
|Extension 2: Before example example1 |
|Before example1       |
|In example 1          |
|After example1        |
|Extension 2: After example example1 |
|Extension 1: After example example1 |
|Extension 1: Before example example2 |
|Extension 2: Before example example2 |
|Before example2       |
|In example 2          |
|After example2        |
|Extension 2: After example example2 |
|Extension 1: After example example2 |
|After specification   |
|Extension 1: afterProcessingSpecification |
|Extension 2: afterProcessingSpecification |
 

[checkRows2]: - "c:verifyRows=#line : getListenerLog()"
[text]:      - "?=#line"
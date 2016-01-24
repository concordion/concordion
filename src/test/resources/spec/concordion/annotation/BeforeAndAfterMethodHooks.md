# Before and After Hooks

Concordion provides hooks to invoke fixture methods before or after an __example__ or __specification__.

## Example hooks 

Annotate a fixture method with `@BeforeExample` to invoke it before each example, or `@AfterExample` to invoke it after each example.

Optionally, the method can have a String parameter annotated with `@ExampleName`, which will be supplied with the name of the example. For example: 

    @BeforeExample
    public void setupExample(@ExampleName exampleName) {
    }    

## Specification hooks 

Annotate a fixture method with `@BeforeSpecification` to invoke it before any of the examples are executed, or `@AfterSpecification` to invoke it after all the examples are executed.

## [Example] (- "example-hooks")

Running the specification [BeforeAndAfterLogging](BeforeAndAfterLogging.md "c:run") which logs messages in its `@BeforeSpecification`, `@BeforeExample`, `@AfterExample` and `@AfterSpecification` methods, as well as in the specification and examples, results in the following : 

|[checkRows][] [text][]|
|----------------------|
|Before specification  |
|Before [Concordion Specification for 'BeforeAndAfterLogging']|
|In specification      |
|After [Concordion Specification for 'BeforeAndAfterLogging']|
|Before example1       |
|In example 1          |
|After example1        |
|Before example2       |
|In example 2          |
|After example2        |
|After specification   |
 

[checkRows]: - "c:verifyRows=#line : getLog()"
[text]:      - "?=#line"
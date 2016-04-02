# Expected To Fail with Example

The fixture implementation status (`@ExpectedToFail` and `@Unimplemented`) is only supported when there are no example commands in the specification.

Since the fixture implementation status is checked at the JUnit test level, it does not makes sense to apply it when there are multiple examples.

When there are example commands in the specification, they can use the "status" attribute on the example command to set the implementation status of the example.

See https://github.com/concordion/concordion/issues/189.

[Should fail](- "c:assertTrue=false")

# [Example 1](-)
[Success](- "c:assertTrue=true")

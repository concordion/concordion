# Test class for Before and After

Log the message '[In specification](- "log(#TEXT)")'

## [Before Each Example] (- "before")

This should run before both Example 1 and Example 2. Log the message '[In before example](- "log(#TEXT)")'

## [Example 1] (- "example1 c:status=ExpectedToFail")

Log the message '[In example 1](- "log(#TEXT)")' and [throw exception](- "error()")

## [Example 2] (- "example2 c:status=ExpectedToFail")

Log the message '[In example 2](- "log(#TEXT)")' and [fail](- "?=ok()")

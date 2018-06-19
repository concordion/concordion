# href updates

When running a non-HTML specification, the file suffix in the `href` link is updated to `html`.

## [Success](-)
 
After running [Success.md](- "#result=simulateRun(#TEXT)"), the output specification should link to a [valid](- "c:assertTrue=#result.isOutputGeneratedBoolean")
 [Success.html](- "?=#result.elementUrl") file.

## ~~Success~~

## [Failure](-)
 
After running [Failure.md](- "#result=simulateRun(#TEXT)"), the output specification should link to a [valid](- "c:assertTrue=#result.isOutputGeneratedBoolean")
 [Failure.html](- "?=#result.elementUrl") file.

## ~~Failure~~

This rule also applies if the specification is set to fail-fast.

## [Fail Fast](-)
 
After running [FailFast.md](- "#result=simulateRun(#TEXT)"), the output specification should link to a [valid](- "c:assertTrue=#result.isOutputGeneratedBoolean")
[FailFast.html](- "?=#result.elementUrl") file.
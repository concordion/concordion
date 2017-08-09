# Specification calling another with Fail Fast example 

This specification calls another specification that fails fast on an example that is expected to fail.

Running [FailFastExample.html](- "#result=simulateRun(#TEXT)"), we expect [1](- "?=#result.successCount") success and [1](- "?=#result.ignoredCount") ignored.
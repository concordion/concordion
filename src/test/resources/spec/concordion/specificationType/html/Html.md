# HTML

The canonical format for Concordion specifications is HTML. The specification must be a well-formed XHTML document.

## Instrumentation

Concordion commands are specified as attributes on elements in the XHTML document. Web browsers ignore attributes that they don't understand, so these commands are effectively invisible.

The commands use a "concordion" namespace defined at the top of each document as follows:

    <html xmlns:concordion="http://www.concordion.org/2007/concordion">

An example command is:

    <p concordion:assert-equals="getGreeting()">Hello World!</p>

To reduce the amount of typing required, the namespace prefix is often shortened to `c`:

    <html xmlns:c="http://www.concordion.org/2007/concordion">

, which shortens our example to:

    <p c:assert-equals="getGreeting()">Hello World!</p>

## Further details
        
* Commands can be [lower case](LowerCaseCommands.html "c:run").


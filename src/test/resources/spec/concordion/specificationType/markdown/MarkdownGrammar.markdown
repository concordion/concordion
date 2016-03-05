# Concordion Markdown Grammar
_Since_: Concordion 2.0.0

With the Markdown extension, Concordion commands are expressed using Markdown links, for example:

`[value](- "command")`

This format separates the instrumentation (`command`) from the specification (`value`). When viewed with a Markdown editor only the specification is shown, with the instrumentation displayed as a title when you hover over the `value`. The URL has to be set to `-`, otherwise it will be treated as a normal Markdown link.

The Markdown extension converts the Markdown to XHTML, with the instrumentation as attributes, prior to running Concordion with the XHTML format specification as input.

This specification describes the grammar for the Markdown instrumentation and checks that it generates correctly formatted instrumentation in XHTML.   

## Link Styles
The extension supports both inline links and reference links.

In both styles, the text `value` is delimited by square brackets.

### Inline Links
Inline links have a set of regular parentheses immediately after the link text’s closing square bracket. Inside the parentheses are a `-` for the URL followed by a space followed by the command, surrounded in quotes (either single or double quotes are allowed). For example:

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[Jane Doe](- '#name')</td>
      <td>&lt;span concordion:set="#name"&gt;Jane Doe&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### Reference Links
As an alternative to inline links, reference links can be used. This can help make the Markdown source more readable, especially for table headers or lengthy commands.

Reference links have a set of square brackets immediately after the link text’s closing square bracket. The link definition can then be placed anywhere in the document. See the [Markdown syntax page](https://daringfireball.net/projects/markdown/syntax#link) for more details. 

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>My name is [Jane Doe][name]<br/>
<br/>
<br/>
[name]: - "?=getFullName()"</td>
      <td>My name is &lt;span concordion:assert-equals="getFullName()"&gt;Jane Doe&lt;/span&gt;</td>
    </tr>
  </table>
</div>

If the link text is the same as the link name, you can use implicit links:

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>will be [split][] into<br/>
<br/>
<br/>
[split]: - "#result = split(#name)"</td>
      <td>will be &lt;span concordion:execute="#result = split(#name)"&gt;split&lt;/span&gt; into </td>
    </tr>
  </table>
</div>

## Commands

### Set Command

The [set](http://concordion.org/Tutorial.html#set) command is expressed using the syntax: `[value](- "#varname")` or `[value](- '#varname')`

which sets the variable named `varname` to the value `value`.

You can also use the long-hand `[value](- "c:set=#varname")` variant if you wish. (Note that, unlike HTML, the `#varname` value is not enclosed in additional quotes.)

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[1](- "#x")</td>
      <td>&lt;span concordion:set="#x"&gt;1&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Bob Smith](- '#name')</td>
      <td>&lt;span concordion:set="#name"&gt;Bob Smith&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Jane Doe](- "c:set=#name")</td>
      <td>&lt;span concordion:set="#name"&gt;Jane Doe&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### assert-equals command

The [assert-equals](http://concordion.org/Tutorial.html#assert-equals) command is expressed using the syntax: `[value](- "?=expression")` or `[value](- '?=expression')`

which asserts that the result of evaluating `expression` equals the `value`.

You can also use the long-hand `[value](- "c:assert-equals=expression")` variant if you wish. 

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[1](- "?=#x")</td>
      <td>&lt;span concordion:assert-equals="#x"&gt;1&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Bob Smith](- '?=#name')</td>
      <td>&lt;span concordion:assert-equals="#name"&gt;Bob Smith&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[3](- "?=add(#x, #y)")</td>
      <td>&lt;span concordion:assert-equals="add(#x, #y)"&gt;3&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Hello](- "?=getGreeting()")</td>
      <td>&lt;span concordion:assert-equals="getGreeting()"&gt;Hello&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Hello](- "?=greeting")</td>
      <td>&lt;span concordion:assert-equals="greeting"&gt;Hello&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Yo](- 'c:assert-equals=greet(#firstName, #lastName)')</td>
      <td>&lt;span concordion:assert-equals="greet(#firstName, #lastName)"&gt;Yo&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### Execute command

The [execute](http://concordion.org/Tutorial.html#execute) command is expressed using the syntax: `[value](- "expression")` or `[value](- 'expression')`

which executes the `expression` (as long as `expression` doesn't start with `c:` as described below). 

You can also use the long-hand `[value](- 'c:execute=expression')` variant if you wish. 

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[When I apply](- "apply()")</td>
      <td>&lt;span concordion:execute="apply()"&gt;When I apply&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[the time is](- "setTime(#date, #time)")</td>
      <td>&lt;span concordion:execute="setTime(#date, #time)"&gt;the time is&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[The greeting for](- "#msg=getGreeting()")</td>
      <td>&lt;span concordion:execute="#msg=getGreeting()"&gt;The greeting for&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[The greeting for](- "#msg=greeting")</td>
      <td>&lt;span concordion:execute="#msg=greeting"&gt;The greeting for&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[Do something](- 'c:execute=doSomething()')</td>
      <td>&lt;span concordion:execute="doSomething()"&gt;Do something&lt;/span&gt;</td>
    </tr>
  </table>
</div>

If the special variable `#TEXT` is used as a parameter within the _expression_, Concordion will pass the _value_ as the parameter.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[09:00AM](- "setCurrentTime(#TEXT)")</td>
      <td>&lt;span concordion:execute="setCurrentTime(#TEXT)"&gt;09:00AM&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### Expression-only commands 
Some commands only require an expression and don't need a text value to be passed. However, Markdown links always require link text.

Any text value that starts with italics will be set to an empty text value in the output specification.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[_set time_](- "setCurrentTime(#time)")</td>
      <td>&lt;span concordion:execute="setCurrentTime(#time)"&gt;&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### Execute on a table
To run the [execute command on a table](http://concordion.org/Tutorial.html#executeTable), the execute command is specified in the first table header column, followed by the command for that column (if any), with the commands for each column of the table specified in the table header.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
<pre>      
|[_add_](- "#z=add(#x, #y)")[Number 1](- "#x")|[Number 2](- "#y")|[Result](- "?=#z")|
| ------------------------------------------: | ---------------: | ---------------: |
|                                            1|                 0|                 1|
|                                            1|                -3|                -2|
</pre>
      </td>
      <td>
<![CDATA[<table concordion:execute="#z=add(#x, #y)">
  <thead>
    <tr>
      <th align="right" concordion:set="#x">Number 1</th>
      <th align="right" concordion:set="#y">Number 2</th>
      <th align="right" concordion:assert-equals="#z">Result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="right">1</td>
      <td align="right">0</td>
      <td align="right">1</td>
    </tr>
    <tr>
      <td align="right">1</td>
      <td align="right">-3</td>
      <td align="right">-2</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>

Using reference style links can make the Markdown source for the table more readable:

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
<pre>      
|[add][][Number 1](- "#x")|[Number 2](- "#y")|[Result](- "?=#z")|
| ----------------------: | ---------------: | ---------------: |
|                        4|                 3|                 7|

[add]: - "#z=add(#x, #y)"
</pre>
      </td>
      <td>
<![CDATA[<table concordion:execute="#z=add(#x, #y)">
  <thead>
    <tr>
      <th align="right" concordion:set="#x">Number 1</th>
      <th align="right" concordion:set="#y">Number 2</th>
      <th align="right" concordion:assert-equals="#z">Result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="right">4</td>
      <td align="right">3</td>
      <td align="right">7</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>

or even:

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
<pre>      
|[add][][Number 1][]|[Number 2][]|[Result][]|
| ----------------: | ---------: | -------: |
|                  4|           3|         7|

[Number 1]: - "#x"
[Number 2]: - "#y"
[add]:      - "#z=add(#x, #y)"
[Result]:   - "?=#z"
</pre>
      </td>
      <td>
<![CDATA[<table concordion:execute="#z=add(#x, #y)">
  <thead>
    <tr>
      <th align="right" concordion:set="#x">Number 1</th>
      <th align="right" concordion:set="#y">Number 2</th>
      <th align="right" concordion:assert-equals="#z">Result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="right">4</td>
      <td align="right">3</td>
      <td align="right">7</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>


### Verify Rows
To run the [verifyRows](http://concordion.org/Tutorial.html#verifyRows) command, the verifyRows command is specified in the first table header column, followed by the command for that column (if any), with the commands for each column of the table specified in the table header.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
        <pre>      
|[_check GST_ ](- "c:verifyRows=#detail:getInvoiceDetails()")[Sub Total](- "?=#detail.subTotal")|[GST](- "?=#detail.gst")|
| --------------------------------- | ---------------------: |
|                                100|                      15|
|                                500|                      75|
|                                 20|                       2|
        </pre>
      </td>
      <td>
<![CDATA[<table concordion:verifyRows="#detail:getInvoiceDetails()">
<thead>
    <tr>
      <th concordion:assert-equals="#detail.subTotal">Sub Total</th>
      <th align="right" concordion:assert-equals="#detail.gst">GST</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>100</td>
      <td align="right">15</td>
    </tr>
    <tr>
      <td>500</td>
      <td align="right">75</td>
    </tr>
    <tr>
      <td>20</td>
      <td align="right">2</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>

Using reference style links can make the Markdown source for the table more readable:

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
        <pre>      
|[_check GST_][][Sub Total][]|[GST][]|
| -------------------------- | ----: |
|                         100|     15|
|                         500|     75|
|                          20|      2|

[_check GST_]: - "c:verifyRows=#detail:getInvoiceDetails()"
[Sub Total]:    - "?=#detail.subTotal"
[GST]:          - "?=#detail.gst"
        </pre>
      </td>
      <td>
<![CDATA[<table concordion:verifyRows="#detail:getInvoiceDetails()">
<thead>
    <tr>
      <th concordion:assert-equals="#detail.subTotal">Sub Total</th>
      <th align="right" concordion:assert-equals="#detail.gst">GST</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>100</td>
      <td align="right">15</td>
    </tr>
    <tr>
      <td>500</td>
      <td align="right">75</td>
    </tr>
    <tr>
      <td>20</td>
      <td align="right">2</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>


The verifyRows command also allows a [strategy](http://concordion.github.io/concordion/latest/spec/command/verifyRows/strategies/Strategies.html) to be specified.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
        <pre>      
|[_check GST_][][Sub Total][]|[GST][]|
| -------------------------- | ----: |
|                         100|     15|

[_check GST_]: - "c:verifyRows=#detail:getInvoiceDetails() c:matchStrategy=BestMatch"
[Sub Total]:    - "?=#detail.subTotal"
[GST]:          - "?=#detail.gst"

        </pre>
      </td>
      <td>
<![CDATA[<table concordion:verifyRows="#detail:getInvoiceDetails()" concordion:matchStrategy="BestMatch">
<thead>
    <tr>
      <th concordion:assert-equals="#detail.subTotal">Sub Total</th>
      <th align="right" concordion:assert-equals="#detail.gst">GST</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>100</td>
      <td align="right">15</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>

### Run
The [Run command](http://concordion.org/Tutorial.html#concordion:run) is expressed using the form:

`[Display text](spec.html "c:run")`

where `spec.html` is the name of the specification. 

To specify a custom runner, use:

`[Display text](spec.html "c:run=runnerName")`

where `runnerName` is the fully qualified class name of the runner.

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
        <pre>      
[Whatever](whatever.html "c:run")
        </pre>
      </td>
      <td>
<![CDATA[<a href="whatever.html" concordion:run="concordion">Whatever</a>]]>     
      </td>
    </tr>
    <tr>
      <td>
        <pre>      
[Whatever](whatever.html "c:run=exampleRunner")
        </pre>
      </td>
      <td>
<![CDATA[<a href="whatever.html" concordion:run="exampleRunner">Whatever</a>]]>     
      </td>
    </tr>
  </table>
</div>

### Example Command
_Since_: Concordion 2.0.0

The [example](http://concordion.github.io/concordion/latest/spec/command/example/Examples.html) command is expressed using the form:

`### [Header text](- "example name")`

which creates:

```
<div concordion:example="example name">
    <h3>Header text</h3>
```    

where `###` can be any level of [header](https://daringfireball.net/projects/markdown/syntax#header), using either atx or setext syntax.

You can also apply an status of [ExpectedToFail](http://concordion.github.io/concordion/latest/spec/command/example/Examples.html#expectedToFail) or [Unimplemented](http://concordion.github.io/concordion/latest/spec/command/example/Examples.html#unimplemented) to the example, for example:

`### [Header text](- "example name c:status=ExpectedToFail")`

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th>Description</th>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>

    <tr>
      <td>h4 example using atx-style syntax</td>
      <td>
        <pre>      
#### [Example 1](- "calculator")
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="calculator"> <h4>Example 1</h4> <p>x</p>]]>&lt;/div>   
      </td>
    </tr>

    <tr>
      <td>h1 example using setext-style syntax</td>
      <td>
        <pre>      
[Example 3](- "setext")
=====================================================
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="setext"> <h1>Example 3</h1> <p>x</p>]]>&lt;/div>   
      </td>
    </tr>
    <tr>
      <td>Example with `ExpectedToFail` status</td>
      <td>
        <pre>      
## [Cancel Transaction](- "cancel tx c:status=ExpectedToFail")
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="cancel tx" concordion:status="ExpectedToFail"> <h2>Cancel Transaction</h2>]]>&lt;/div>
      </td>
    </tr>
    
  </table>
</div>  

#### Closing an example
The example block continues until it is closed either implicitly or explicitly.

An example is implicitly closed on any of these conditions:

* another example starts, or
* a header is encountered that is at a higher level than the example header (eg. the example is a `h3` and a `h2` header is encountered), or
* the end of file is reached.

To explicitly close an example, create a header with the example heading struck-through. For example:  

    ## ~~My Example~~
    
will close the example with the heading `My Example`.


<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th>Description</th>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>

    <tr>
      <td>Example automatically ended by start of another example</td>
      <td>
        <pre>      
#### [Example 1](- "calculator")
x
#### [Example 2](- "another")
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="calculator"> <h4>Example 1</h4> <p>x</p>]]>&lt;/div>
<![CDATA[<div concordion:example="another"> <h4>Example 2</h4>]]>&lt;/div>    
      </td>
    </tr>

    <tr>
      <td>Example is not automatically ended by a lower-level heading</td>
      <td>
        <pre>      
#### [Example 1](- "calculator")
x
##### Subheading
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="calculator"> <h4>Example 1</h4> <p>x</p> <h5>Subheading</h5>]]>&lt;/div> 
      </td>
    </tr>

    <tr>
      <td>Example is automatically ended by a higher-level heading</td>
      <td>
        <pre>      
### [Example on h3](- "ex3")
My example
## head2
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="ex3"> <h3>Example on h3</h3>]]>
&lt;p>My example&lt;/p>&lt;/div>
&lt;h2>head2&lt;/h2>
      </td>
    </tr>

    <tr>
      <td>Example ended by a strikethrough heading with the same title as the example</td>
      <td>
        <pre>      
# [Example 1](- "calculator")
x
# ~~Example 1~~
y
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="calculator"> <h1>Example 1</h1> <p>x</p>]]>&lt;/div>
&lt;p>y&lt;/p>     
      </td>
    </tr>
  </table>
</div>

### Arbitrary Commands
Any Concordion command can be included in the title of the Markdown link by using the prefix `c:`.

Note that, unlike HTML format specifications, the expression is not enclosed in additional quotes. Neither are the value of any additional parameters passed to the command.

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>
        <pre>
[value](- "c:command=expression")              
        </pre>
      </td>
      <td>
<![CDATA[<span concordion:command="expression">value</span>]]>
      </td>
    </tr>
    <tr>
      <td>
        <pre>
[value](- "c:command=expression param=x")              
        </pre>
      </td>
      <td>
<![CDATA[<span concordion:command="expression" param="x">value</span>]]>
      </td>
    </tr>
    <tr>
      <td>
        <pre>
[value](- "c:command=expression param1=x param2=y")              
        </pre>
      </td>
      <td>
<![CDATA[<span concordion:command="expression" param1="x" param2="y">value</span>]]>
      </td>
    </tr>
  </table>
</div>

## Non-Concordion links
The set, assert-equals and execute commands require the link URL to be set to `-`. Links with other URLs are not modified. For example:

<div class="example">
  <h3>Example</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[John](- "#a")</td>
      <td>&lt;span concordion:set="#a"&gt;John&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[John](john.html)</td>
      <td>&lt;a href="john.html"&gt;John&lt;/a&gt;</td>
    </tr>
    <tr>
      <td>[John](john.html "Details about John")</td>
      <td>&lt;a href="john.html" title="Details about John"&gt;John&lt;/a&gt;</td>
    </tr>
    <tr>
      <td>[John](john.html "#More about John")</td>
      <td>&lt;a href="john.html" title="#More about John"&gt;John&lt;/a&gt;</td>
    </tr>
    <tr>
      <td>[John](-.html "Weird URL")</td>
      <td>&lt;a href="-.html" title="Weird URL"&gt;John&lt;/a&gt;</td>
    </tr>
    <tr>
      <td>[John](.)</td>
      <td>&lt;a href="."&gt;John&lt;/a&gt;</td>
    </tr>
    <tr>
      <td>[John](#)</td>
      <td>&lt;a href="#"&gt;John&lt;/a&gt;</td>
    </tr>
  </table>
</div>

## Support for Concordion commands in other namespaces, eg extensions
In order to include Concordion commands with namespaces other than the default Concordion namespaces, such as those available in extensions, you must declare the namespace.

Add the `@ConcordionOptions` annotation to the fixture class, with the `declareNamespaces` element set to a list of strings, where the values alternate between namespace prefixes and the namespace they are mapped to. For example:

    @RunWith(ConcordionRunner.class)
    @ConcordionOptions(declareNamespaces={"ext", "urn:concordion-extensions:2010"})
    public class MyFixture

The namespaced command can then be included in the title of the Markdown link, using the prefix declared in the annotation.

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[-](- "ext:embed=getDetails()")</td>
      <td>&lt;span ext:embed="getDetails()"&gt;-&lt;/span&gt;</td>
    </tr>
  </table>
</div>

## Additional checks

### Multiple commands on a single line
Multiple commands on the same line are supported.

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[1](- "#x") + [2](- "#y") = [3](- "?=add(#x,#y)")</td>
      <td>&lt;span concordion:set="#x"&gt;1&lt;/span&gt; + &lt;span concordion:set="#y"&gt;2&lt;/span&gt; = &lt;span concordion:assert-equals="add(#x,#y)"&gt;3&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[3](- "?=three()"). [Fred](- "#name").</td>
      <td>&lt;span concordion:assert-equals="three()"&gt;3&lt;/span&gt;. &lt;span concordion:set="#name"&gt;Fred&lt;/span&gt;.</td>
    </tr>
  </table>
</div>


### HTML entities
HTML entities in the text value are encoded correctly.

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
      <td>[&amp; &lt; 3](- "#x")</td>
      <td>&lt;span concordion:set="#x"&gt;&amp;amp; &amp;lt; 3&lt;/span&gt;</td>
    </tr>
  </table>
</div>

### Extended character set

<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>
    <tr>
        <td>&#233;</td>
        <td>é</td>
    </tr>
  </table>
</div>




##TODO

### Document embedded HTML for execute on a list and complex sentence structures 


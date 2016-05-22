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

* [assert-equals](MarkdownAssertEqualsCommand.md "c:run")
* [example](MarkdownExampleCommand.md "c:run")
* [execute](MarkdownExecuteCommand.md "c:run")
* [run](MarkdownRunCommand.md "c:run")
* [set](MarkdownSetCommand.md "c:run")
* [verify-rows](MarkdownVerifyRowsCommand.md "c:run")

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

### Embedding HTML

For cases where the Markdown syntax is too restrictive, you can [inline HTML](https://daringfireball.net/projects/markdown/syntax#html).

This can be used to embed Concordion structures that are not currently supported by the Markdown syntax.

<a id="execute-on-a-list"/>
#### Execute on a list

The [execute on a list](http://concordion.github.io/concordion/latest/spec/command/execute/ExecutingList.html) command can be implemented using the HTML list structure, wrapped in a `<div>` element. For example:

    <div>
        <ol concordion:execute="parseNode(#TEXT, #LEVEL)">
            <li>Europe</li>
            <ul>
                <li>Austria</li>
                <ol>
                    <li>Vienna</li>
                </ol>
                <li>UK</li>
                <ul>
                    <li>England</li>
                    <li>Scotland</li>
                </ul>
                <li>France</li>
            </ul>
            <li>Australia</li>
        </ol>
    </div>

#### Handling unusual sentence structures

In order to [handle unusual sentence structures](http://concordion.org/Tutorial.html#executeUnusualSentences), Concordion allows you to use an `execute` command on an outer HTML element. When using Markdown, this can be implemented using the HTML structure wrapped in a `<div>`.

    <div>
        <p concordion:execute="#greeting = greetingFor(#firstName)">
            The greeting "<span concordion:assertEquals="#greeting">Hello Bob!</span>"
            should be given to user <span concordion:set="#firstName">Bob</span>
            when he logs in.
        </p>
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
HTML entities in the text value are automatically escaped.

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
    <tr>
      <td>[&lt;div&gt;](- "#d")</td>
      <td>&lt;span concordion:set="#d"&gt;&amp;lt;div&amp;gt;&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&lt;div class="x"&gt;](- "#d")</td>
      <td>&lt;span concordion:set="#d"&gt;&amp;lt;div class=&amp;quot;x&amp;quot;&amp;gt;&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&lt;request&gt;](- "#d")</td>
      <td>&lt;span concordion:set="#d"&gt;&amp;lt;request&amp;gt;&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&lt;div&gt;&lt;br/&gt;&lt;/div&gt;](- "#d")</td>
      <td>&lt;span concordion:set="#d"&gt;&amp;lt;div&amp;gt;&amp;lt;br/&amp;gt;&amp;lt;/div&amp;gt;&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&lt;?xml version="1.0" encoding="UTF-8"?&gt;](- "#decl")</td>
      <td>&lt;span concordion:set="#decl"&gt;&amp;lt;?xml version="1.0" encoding="UTF-8"?&amp;gt;&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&amp; &lt; 3](- "c:set=#x")</td>
      <td>&lt;span concordion:set="#x"&gt;&amp;amp; &amp;lt; 3&lt;/span&gt;</td>
    </tr>
    <tr>
      <td>[&lt;div&gt;](- "c:set=#d")</td>
      <td>&lt;span concordion:set="#d"&gt;&amp;lt;div&amp;gt;&lt;/span&gt;</td>
    </tr>

    <tr>
<td><pre>
[&amp; &lt; 3][]

[&amp; &lt; 3]: - "c:set=#x"
</pre></td>
      <td>&lt;span concordion:set="#x"&gt;&amp;amp; &amp;lt; 3&lt;/span&gt;</td>
    </tr>

    <tr>
<td><pre>
[&lt;div&gt;][xx]

[xx]: - "#d"
</pre></td>
       <td>&lt;span concordion:set="#d"&gt;&amp;lt;div&amp;gt;&lt;/span&gt;</td>
    </tr>

    <tr>
<td><pre>
|[foo](- "foo()")|[a &lt; b](- "#d")|[&lt;x&gt;](- "?=#d")|
| -------------- | ------------- | ------------- |
| bar            |       a &lt; b   |            &lt;x&gt;|
</pre></td>
      <td>&lt;table concordion:execute="foo()"&gt; &lt;thead&gt; &lt;tr&gt; &lt;th&gt;&lt;/th&gt; &lt;th concordion:set="#d"&gt;a &amp;lt; b&lt;/th&gt; &lt;th concordion:assert-equals="#d"&gt;&amp;lt;x&amp;gt;&lt;/th&gt; &lt;/tr&gt; &lt;/thead&gt; &lt;tbody&gt; &lt;tr&gt; &lt;td&gt;bar &lt;/td&gt; &lt;td&gt;a &amp;lt; b &lt;/td&gt; &lt;td&gt;&amp;lt;x&amp;gt;&lt;/td&gt; &lt;/tr&gt; &lt;/tbody&gt; &lt;/table&gt;</td>
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
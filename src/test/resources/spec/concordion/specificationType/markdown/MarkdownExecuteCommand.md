# Markdown execute command
_Since_: Concordion 2.0.0

The [execute command](../../command/execute/Execute.html) is expressed using the syntax: `[value](- "expression")` or `[value](- 'expression')`

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

<a id="execute-on-a-table"/>
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

The first column doesn't have to have a concordion command associated with it:

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
|[add][]Description|[Number 1][]|[Number 2][]|[Result][]|
| ---------------- | ---------------- | --------- | ------- |
| Simple sum       |                 4|          3|        7|

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
      <th>Description</th>
      <th concordion:set="#x">Number 1</th>
      <th concordion:set="#y">Number 2</th>
      <th concordion:assert-equals="#z">Result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Simple sum </td>
      <td>4</td>
      <td>3</td>
      <td>7</td>
    </tr>
  </tbody>
</table>]]>     
      </td>
    </tr>
  </table>
</div>
# Markdown assert-equals command
_Since_: Concordion 2.0.0

The [assert-equals command](../../command/assertEquals/AssertEquals.html) is expressed using the syntax: `[value](- "?=expression")` or `[value](- '?=expression')`

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
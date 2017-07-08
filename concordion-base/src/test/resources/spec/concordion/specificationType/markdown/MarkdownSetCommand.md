# Markdown set Command
_Since_: Concordion 2.0.0

The [set command](../../command/set/Set.html) is expressed using the syntax: `[value](- "#varname")` or `[value](- '#varname')`

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
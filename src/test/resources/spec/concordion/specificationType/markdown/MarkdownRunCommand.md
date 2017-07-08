# Markdown run command
_Since_: Concordion 2.0.0

### Run
The [Run command](../../command/run/Run.html) is expressed using the form:

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
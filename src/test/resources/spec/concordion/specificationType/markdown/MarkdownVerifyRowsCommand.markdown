# Markdown verify-rows command 
_Since_: Concordion 2.0.0

To run the [verifyRows command](../../command/verifyRows/VerifyRows.html), the verifyRows command is specified in the first table header column, followed by the command for that column (if any), with the commands for each column of the table specified in the table header.

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
# Markdown example command
_Since_: Concordion 2.0.0

The [example command](../../command/example/Example.html) is expressed using the form:

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
[Example 2](- "setext")
=====================================================
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="setext"> <h1>Example 2</h1> <p>x</p>]]>&lt;/div>   
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
    
#### Auto-generated example name
If the example name is not specified, it will be generated from the header text. All alphanumeric characters from the header name will be included, with the alphabetic characters lowercased. Other characters will be converted to hyphens, with hyphens removed from the start and end of the header name.
    
<div class="example">
  <h3>Examples</h3>
  <table concordion:execute="#html=translate(#md)">
    <tr>
      <th>Description</th>
      <th concordion:set="#md">Markdown</th>
      <th concordion:assert-equals="#html">Resultant HTML</th>
    </tr>

    <tr>
      <td>Example with empty name</td>
      <td>
        <pre>      
## [Example 3](- "")
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="example-3"> <h2>Example 3</h2> <p>x</p>]]>&lt;/div>   
      </td>
    </tr>
    
    <tr>
      <td>Example with weird characters</td>
      <td>
        <pre>      
## [3. Hmmm !@#$%^*( ](- "")
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="3-hmmm"> <h2>3. Hmmm !@#$%^*( </h2> <p>x</p>]]>&lt;/div>   
      </td>
    </tr>

    <tr>
      <td>Example with multiple dashes</td>
      <td>
        <pre>      
## [- A - B - - C -- -D - ](- "")
x
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="a-b-c-d"> <h2>- A - B - - C -- -D - </h2> <p>x</p>]]>&lt;/div>   
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

    <tr>
      <td>Example with single quote in name ended by a strikethrough heading with the same title as the example</td>
      <td>
        <pre>
# [Example 'One'](-)
x
# ~~Example 'One'~~
y
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="example-one"> <h1>Example &#39;One&#39;</h1> <p>x</p>]]>&lt;/div>
&lt;p>y&lt;/p>
      </td>
    </tr>

    <tr>
      <td>Example with XML element in name ended by a strikethrough heading with the same title as the example</td>
      <td>
        <pre>
# [Example &lt;One&gt;](-)
x
# ~~Example &lt;One&gt;~~
y
        </pre>
      </td>
      <td>
<![CDATA[<div concordion:example="example-one"> <h1>Example &lt;One&gt;</h1> <p>x</p>]]>&lt;/div>
&lt;p>y&lt;/p>
      </td>
    </tr>
  </table>
</div>
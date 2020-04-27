# Flexmark Options
_Since_: Concordion 3.0.0

The Java version of Concordion uses the [Flexmark](https://github.com/vsch/flexmark-java) parser which provides a range of [extensions and configuration options](https://github.com/vsch/flexmark-java/wiki/Extensions). 

Concordion allows you to add and configure these options using the field level annotation `@FlexmarkOptions`. 
The field which is annotated must be castable to type `DataHolder`. See [configuring options](https://github.com/vsch/flexmark-java/wiki/Extensions#configuring-options) for details (you only need to create a field similar to the `OPTIONS` field. Concordion creates the `PARSER` and `RENDERER`). 

<div class="example" xmlns:c="http://www.concordion.org/2007/concordion">
  <h3>Example</h3>
    <p>Executing the following fixture:</p>
    <pre class="java" concordion:set="#javaFragment">
import java.util.Arrays;    
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.concordion.api.option.FlexmarkOptions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)<br/>

public class ExampleFixture {<br/>
  @FlexmarkOptions<br/>
  DataSet flexmarkOptions = new MutableDataSet()<br/>
      .set(EmojiExtension.ATTR_IMAGE_SIZE, "24")<br/>
      .set(Parser.EXTENSIONS, Arrays.asList(EmojiExtension.create()));<br/>

}    
   </pre>
   
with the specification:

<pre class="markdown" concordion:set="#markdownFragment">
Is this feature :thumbsup: or :thumbsdown:?
</pre>
  
<p>when <span concordion:execute="#result = process(#markdownFragment, #javaFragment)">processed</span>, results in the specification:</p>

<pre class="html" c:assert-equals="#result">
Is this feature &lt;img src="/img/thumbsup.png" alt="emoji people:thumbsup" height="24" width="24" align="absmiddle" /&gt; or &lt;img src="/img/thumbsdown.png" alt="emoji people:thumbsdown" height="24" width="24" align="absmiddle" /&gt;?
</pre>

</div>
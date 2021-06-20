package org.concordion.api.option;

import com.vladsch.flexmark.profile.pegdown.Extensions;

/**
 * Extensions to the Markdown language that can be enabled for Concordion Markdown.
 * <p>
 * Note that the following extensions are enabled by default:
 * <ul>
 * <li>TABLES - similar to what <a href="http://fletcherpenney.net/multimarkdown/users_guide/">MultiMarkdown</a> offers.</li>
 * <li>STRIKETHROUGH - ~~strikethroughs~~ as supported in Pandoc and Github.</li>
 * </ul>
 * <p>
 * The SMARTS, QUOTES and SMARTYPANTS extensions from Pegdown are currently not supported in Concordion Markdown, 
 * since the XOM parser fails due to unknown entity references for these characters.
 * 
 * @since 2.0.0
 */
public enum MarkdownExtensions {
    /**
     * The default, standard markup mode without any extensions.
     */
    NONE(Extensions.NONE),
    /**
     * PHP Markdown Extra style abbreviations.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#abbr">PHP Markdown Extra</a>
     */
    ABBREVIATIONS(Extensions.ABBREVIATIONS),
    /**
     * Enables the parsing of hard wraps as HTML linebreaks. Similar to what Github does.
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    HARDWRAPS(Extensions.HARDWRAPS),
    /**
     * Enables plain autolinks the way github flavoured markdown implements them.
     * With this extension enabled pegdown will intelligently recognize URLs and email addresses
     * without any further delimiters and mark them as the respective link type.
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    AUTOLINKS(Extensions.AUTOLINKS),    
    /**
     * PHP Markdown Extra style definition lists.
     * Additionally supports the small extension proposed in the article referenced below.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#def-list">PHP Markdown Extra</a>
     * @see <a href="http://www.justatheory.com/computers/markup/modest-markdown-proposal.html">Extension proposal</a>
     */
    DEFINITIONS(Extensions.DEFINITIONS),
    /**
     * PHP Markdown Extra style fenced code blocks.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#fenced-code-blocks">PHP Markdown Extra</a>
     */
    FENCED_CODE_BLOCKS(Extensions.FENCED_CODE_BLOCKS),    
    /**
     * Support [[Wiki-style links]]. URL rendering is performed by the active LinkRenderer.
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    WIKILINKS(Extensions.WIKILINKS),
    /**
     * Generate anchor links for headers by taking the first range of alphanumerics and spaces.
     */
    ANCHORLINKS(Extensions.ANCHORLINKS),
    /**
     * Generate anchor links for headers using complete contents of the header.
     * <ul>
     * <li>Spaces and non-alphanumerics replaced by -, multiple dashes trimmed to one.</li>
     * <li>Anchor link is added as first element inside the header with empty content: <h1><a id="header"></a>header</h1></li>
     * </ul>
     */
    EXTANCHORLINKS(Extensions.EXTANCHORLINKS),
    /**
     * Suppresses HTML blocks. They will be accepted in the input but not be contained in the output.
     */
    SUPPRESS_HTML_BLOCKS(Extensions.SUPPRESS_HTML_BLOCKS),
    /**
     * Suppresses inline HTML tags. They will be accepted in the input but not be contained in the output.
     */
    SUPPRESS_INLINE_HTML(Extensions.SUPPRESS_INLINE_HTML),
    /**
     * Suppresses HTML blocks as well as inline HTML tags.
     * Both will be accepted in the input but not be contained in the output.
     */
    SUPPRESS_ALL_HTML(Extensions.SUPPRESS_ALL_HTML),
    /**
     * Requires a space char after Atx # header prefixes, so that #dasdsdaf is not a header.
     */
    ATXHEADERSPACE(Extensions.ATXHEADERSPACE),
    /**
     * Wrap a list item or definition term in <p> tags if it contains more than a simple paragraph.
     */
    FORCELISTITEMPARA(Extensions.FORCELISTITEMPARA),
    /**
     * Allow horizontal rules without a blank line following them.
     */
    RELAXEDHRULES(Extensions.RELAXEDHRULES),
    /**
     * GitHub style task list items: - [ ] and - [x]
     */
    TASKLISTITEMS(Extensions.TASKLISTITEMS);

    
    private int pegdownExtension;

    private MarkdownExtensions(int pegdownExtension) {
        this.pegdownExtension= pegdownExtension;
    }

    public int getPegdownExtension() {
        return pegdownExtension;
    }
}
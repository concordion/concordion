package org.concordion.internal.parser.markdown;

public class LinkNode {

    private final String url;
    private final String title;
    private final String text;

    public LinkNode(String url, String title, String text) {
        this.url = url;
        this.title = title;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}

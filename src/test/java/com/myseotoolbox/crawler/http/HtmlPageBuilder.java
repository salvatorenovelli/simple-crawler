package com.myseotoolbox.crawler.http;

import java.util.List;

public class HtmlPageBuilder {
    StringBuilder body = new StringBuilder();
    StringBuilder header = new StringBuilder();

    StringBuilder selected;

    public HtmlPageBuilder body() {
        selected = body;
        return this;
    }

    public HtmlPageBuilder appendLink(String url) {
        selected.append(toHtmlLink(url));
        return this;
    }

    private String toHtmlLink(String url) {
        return "<a href=\"" + url + "\">" + url + "</a>";
    }

    public String build() {

        StringBuilder out = new StringBuilder();

        out.append("\n").append("<HTML>");
        out.append("\n").append("     <HEADER>");
        out.append("\n").append(header);
        out.append("\n").append("     </HEADER>");
        out.append("\n").append("     <BODY>");
        out.append("\n").append(body);
        out.append("\n").append("     </BODY>");
        out.append("\n").append("</HTML>");
        return out.toString();
    }

    public HtmlPageBuilder appendLink(List<String> links) {
        links.forEach(this::appendLink);
        return this;
    }
}

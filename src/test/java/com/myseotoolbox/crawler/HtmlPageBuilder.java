package com.myseotoolbox.crawler;

public class HtmlPageBuilder {
    StringBuilder body = new StringBuilder();
    StringBuilder header = new StringBuilder();

    StringBuilder selected;

    public HtmlPageBuilder body() {
        selected = body;
        return this;
    }

    public HtmlPageBuilder appendLink(String url) {
        selected.append("<a href=\"" + url + "\">" + url + "</a>");
        return this;
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
}

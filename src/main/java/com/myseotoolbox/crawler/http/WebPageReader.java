package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.RedirectChain;
import com.myseotoolbox.crawler.model.RedirectChainElement;
import com.myseotoolbox.crawler.model.WebPage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static com.myseotoolbox.utils.HTTPUtils.isRedirect;
import static java.nio.charset.StandardCharsets.UTF_8;

public class WebPageReader {

    private final HttpClient client;

    public WebPageReader(HttpClient client) {
        this.client = client;
    }


    public WebPage visit(URI uri) throws RedirectLoopException, IOException {

        RedirectChain curRedirectChain = new RedirectChain();

        URI curUri = uri;
        HttpResponse curResponse;

        do {

            curResponse = client.get(curUri);
            if (isRedirectLoop(curRedirectChain, curUri, curResponse))
                throw new RedirectLoopException(curRedirectChain);

            URI locationHeader = curResponse.getLocationHeader();
            curRedirectChain.add(new RedirectChainElement(curUri, curResponse.getHttpStatus(), locationHeader));
            curUri = locationHeader;

        } while (isRedirect(curResponse.getHttpStatus()));


        String html = fetchHtml(curResponse);


        return new WebPage(uri, curRedirectChain, html);
    }

    private String fetchHtml(HttpResponse curResponse) throws IOException {

        if (curResponse.getHttpStatus() != HttpURLConnection.HTTP_OK) return "";

        return IOUtils.toString(curResponse.getInputStream(), UTF_8.name());
    }

    private boolean isRedirectLoop(RedirectChain chain, URI sourceUri, HttpResponse response) {
        return isRedirect(response.getHttpStatus()) &&
                (
                        chain.contains(response.getLocationHeader()) || response.getLocationHeader().equals(sourceUri)
                );
    }


}

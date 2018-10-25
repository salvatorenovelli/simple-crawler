package com.myseotoolbox.crawler.http;

import java.net.URI;

public interface HttpClient {
    HttpResponse get(URI uri);
}

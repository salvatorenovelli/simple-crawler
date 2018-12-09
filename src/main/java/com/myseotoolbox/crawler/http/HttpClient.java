package com.myseotoolbox.crawler.http;

import java.io.IOException;
import java.net.URI;

public interface HttpClient {
    HttpResponse get(URI uri) throws IOException;
}

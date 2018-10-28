package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.HttpResponse;
import com.myseotoolbox.crawler.model.Page;

import java.net.URI;

public interface HttpClient<T extends Page> {
    HttpResponse<T> get(URI uri);
}

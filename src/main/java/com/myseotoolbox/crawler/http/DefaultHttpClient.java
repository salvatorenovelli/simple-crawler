package com.myseotoolbox.crawler.http;

import com.myseotoolbox.utils.SafeStringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static com.myseotoolbox.utils.HTTPUtils.isRedirect;


@Slf4j
public class DefaultHttpClient implements HttpClient {


    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    public static final String USER_AGENT = "Seo Bot (Java 9/Linux)";


    public HttpResponse get(URI uri) throws IOException {

        HttpURLConnection connection = openConnection(uri);

        URI dstURI = uri;

        int status = connection.getResponseCode();

        if (isRedirect(status)) {
            dstURI = extractDestinationUri(connection, dstURI);
        }


        return new HttpResponse(uri, status, dstURI, status < 400 ? connection.getInputStream() : null);
    }

    private HttpURLConnection openConnection(URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.connect();
        return connection;
    }


    private URI extractDestinationUri(HttpURLConnection connection, URI initialLocation) {
        String locationHeader = connection.getHeaderField("location");
        URI location;


        if (containsUnicodeCharacters(locationHeader)) {
            log.warn("Redirect destination {} contains non ASCII characters (as required by the standard)", connection.getURL());
            location = URI.create(SafeStringEncoder.encodeString(locationHeader));
        } else {
            location = URI.create(locationHeader);
        }

        if (location.isAbsolute()) {
            return location;
        } else {
            return initialLocation.resolve(location);
        }
    }

    private boolean containsUnicodeCharacters(String locationHeaderField) {
        for (int i = 0; i < locationHeaderField.length(); i++) {
            if (locationHeaderField.charAt(i) >= 128) return true;
        }
        return false;
    }


}
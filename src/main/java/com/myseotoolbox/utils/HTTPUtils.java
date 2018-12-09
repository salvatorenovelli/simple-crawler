package com.myseotoolbox.utils;


import java.net.HttpURLConnection;

public class HTTPUtils {
    public static boolean isRedirect(int statusCode) {
        switch (statusCode) {
            case HttpURLConnection.HTTP_MOVED_PERM: // 301
            case HttpURLConnection.HTTP_MOVED_TEMP: // 302
            case HttpURLConnection.HTTP_SEE_OTHER:  // 303
            case 307:
                return true;
            default:
                return false;
        }
    }
}

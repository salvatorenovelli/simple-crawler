package com.myseotoolbox.utils;

import static org.apache.http.HttpStatus.*;

public class HTTPUtils {
    public static boolean isRedirect(int statusCode) {
        switch (statusCode) {
            case SC_MOVED_PERMANENTLY: // 301
            case SC_MOVED_TEMPORARILY: // 302
            case SC_SEE_OTHER: // 303
            case SC_TEMPORARY_REDIRECT: // 307
                return true;
            default:
                return false;
        }
    }
}

package com.myseotoolbox.utils;

import java.util.function.Predicate;

public class StreamUtils {
    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}

package com.myseotoolbox.crawler.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;


@Slf4j
public class TestUtils {

    private static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static void printObject(Object obj) {
        printObject("", obj);
    }

    public static void printObject(String objectName, Object obj) {

        log.info("\n\n########## Printing Object ##########\n\n" +
                "Name: {}\n" +
                "Type: {}\n\n" +
                "{}", objectName, obj.getClass().getSimpleName(), toJson(obj));

    }

    public static void printMockInvocations(Object mock) {
        log.info(Mockito.mockingDetails(mock).printInvocations());
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String prettyJson(String in) {
        try {
            if (!isJSONValid(in)) return in;
            JSONObject json = new JSONObject(in);
            return json.toString(4);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}

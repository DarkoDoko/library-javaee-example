package com.library.app.commontests.utils;

import java.io.InputStream;
import java.util.Scanner;
import org.json.JSONException;
import org.junit.Ignore;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author ddoko
 */
@Ignore
public class JsonTestUtils {
    
    public static final String BASE_JSON_DIR = "json/";
    
    private JsonTestUtils(){}
    
    public static String readJsonFile(String relativePath){
        InputStream is = JsonTestUtils.class.getClassLoader().getResourceAsStream(BASE_JSON_DIR + relativePath);
        try(Scanner s = new Scanner(is)) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }
    
    public static void assertJsonMatchesExpectedJson(String actulaJson, String expectedJson) {
        try{
            JSONAssert.assertEquals(expectedJson, actulaJson, JSONCompareMode.NON_EXTENSIBLE);
        } catch(JSONException e){
            throw new IllegalArgumentException(e);
        }
    }
}

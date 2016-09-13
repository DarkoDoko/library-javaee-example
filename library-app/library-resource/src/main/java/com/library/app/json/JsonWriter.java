package com.library.app.json;

import com.google.gson.Gson;

/**
 *
 * @author ddoko
 */
public class JsonWriter {

    private JsonWriter(){
    }
    
    public static String writeToString(Object object){
        if(object == null){
            return "";
        }
        return new Gson().toJson(object);
    }
}

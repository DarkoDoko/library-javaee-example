package com.library.app.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author ddoko
 */
public class JsonUtils {

    private JsonUtils(){
    }
    
    public static JsonElement getjsonElementWithId(Long id){
        JsonObject idJson = new JsonObject();
        idJson.addProperty("id", id);
        
        return idJson;
    }
}

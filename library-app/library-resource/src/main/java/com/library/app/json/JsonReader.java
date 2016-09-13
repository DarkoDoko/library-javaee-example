package com.library.app.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.library.app.InvalidJsonException;

/**
 *
 * @author ddoko
 */
public class JsonReader {
    
    public static JsonObject readAsJsonObject(String json) {
        return readJsonAs(json, JsonObject.class);
    }
    
    public static JsonArray readAsJsonArray(String json){
        return readJsonAs(json, JsonArray.class);
    }
    
    public static <T> T readJsonAs(String json, Class<T> jsonClass) throws InvalidJsonException {
        if(json == null || json.trim().isEmpty()){
            throw new InvalidJsonException("Json string can not be null or empty");
        }
        try{
            return new Gson().fromJson(json, jsonClass);
        } catch(JsonSyntaxException e){
            throw new InvalidJsonException(e);
        }
    }
    
    public static Long getLongOrNull(JsonObject jsonObject, String propertyName){
        JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)){
            return null;
        }
        return property.getAsLong();
    }
    
    public static Integer getIntegerOrNull(JsonObject jsonObject, String propertyName){
        JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)){
            return null;
        }
        return property.getAsInt();
    }
    
    public static String getStringOrNull(JsonObject jsonObject, String propertyName){
        JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)){
            return null;
        }
        return property.getAsString();
    }
    
    public static Double getDoubleOrNull(JsonObject jsonObject, String propertyName){
        JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)){
            return null;
        }
        return property.getAsDouble();
    }
    
    private static boolean isJsonElementNull(JsonElement element){
        return element == null || element.isJsonNull();
    }
    
}

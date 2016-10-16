package com.library.app.json;

import com.google.gson.JsonObject;
import com.library.app.common.model.OperationResult;

public class OperationResultJsonWriter {

    private OperationResultJsonWriter() {
    }
    
    public static String toJson(OperationResult result){
        return JsonWriter.writeToString(getJsonObject(result));
    }
    
    private static Object getJsonObject(OperationResult result){
        if(result.isSuccess()){
            return getJsonSuccess(result);
        }
        return getJsonError(result);
    }
    
    private static Object getJsonSuccess(OperationResult result){
        return result.getEntity();
    }
    
    private static JsonObject getJsonError(OperationResult result){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", result.getErrorIdentification());
        jsonObject.addProperty("errorDescription", result.getErrorDescription());
        
        return jsonObject;
    }
    
}

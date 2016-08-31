package com.library.app.common.json;

import com.google.gson.Gson;
import com.library.app.common.model.OperationResult;

/**
 *
 * @author ddoko
 */
public class OperationResultJsonWriter {

    private OperationResultJsonWriter() {
    }
    
    public static String toJson(OperationResult result){
        if(result.getEntity() == null){
            return "";
        }
        
        Gson gson = new Gson();
        return gson.toJson(result.getEntity());
    }
    
}

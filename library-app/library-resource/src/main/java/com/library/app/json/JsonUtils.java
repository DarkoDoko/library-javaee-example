package com.library.app.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.pagination.PaginatedData;

public class JsonUtils {

    private JsonUtils(){
    }
    
    public static JsonElement getJsonElementWithId(Long id){
        JsonObject idJson = new JsonObject();
        idJson.addProperty("id", id);
        
        return idJson;
    }
    
    public static <T> JsonElement getJsonElementWithPagingAndEntries(PaginatedData<T> paginatedData,
                EntityJsonConverter<T> entityJsonConverter) {
        
        JsonObject jsonWithEntriesAndPaging = new JsonObject();
        
        JsonObject jsonPaging = new JsonObject();
        jsonPaging.addProperty("totalRecords", paginatedData.getNumberOfRows());
        
        jsonWithEntriesAndPaging.add("paging", jsonPaging);
        jsonWithEntriesAndPaging.add("entries", entityJsonConverter.convertToJsonElement(paginatedData.getRows()));
        
        return jsonWithEntriesAndPaging;
        
    }
}

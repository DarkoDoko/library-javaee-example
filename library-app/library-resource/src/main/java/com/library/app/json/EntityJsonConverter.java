package com.library.app.json;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public interface EntityJsonConverter<T> {
    
    T convertFrom(String json);

    JsonElement convertToJsonElement(T entity);

    default JsonElement convertToJsonElement(List<T> entities) {
    	JsonArray jsonArray = new JsonArray();

        entities.forEach((entity) -> {
            jsonArray.add(convertToJsonElement(entity));
        });

    	return jsonArray;
    }
}

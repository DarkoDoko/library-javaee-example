package com.library.app.category.resource;

import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.category.model.Category;

/**
 *
 * @author ddoko
 */
public class CategoryJsonConverter {
    public Category convertFrom(String json){
        JsonObject jsonObject = JsonReader.readJsonObject(json);
        Category category = new Category();
        category.setName(JsonReader.getStringOrNull(jsonObject, "name"));
        
        return category;
    }
}

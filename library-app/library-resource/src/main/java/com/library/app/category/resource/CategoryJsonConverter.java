package com.library.app.category.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.json.JsonReader;
import com.library.app.category.model.Category;
import com.library.app.json.EntityJsonConverter;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryJsonConverter implements EntityJsonConverter<Category> {

    @Override
    public Category convertFrom(String json){
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);
        
        Category category = new Category();
        category.setName(JsonReader.getStringOrNull(jsonObject, "name"));
        
        return category;
    }

    @Override
    public JsonElement convertToJsonElement(Category category) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());
        return jsonObject;
    }
    
}

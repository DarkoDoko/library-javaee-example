package com.library.app.author.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.json.EntityJsonConverter;
import com.library.app.json.JsonReader;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorJsonConverter implements EntityJsonConverter<Author>{

    @Override
    public Author convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        Author author = new Author();
        author.setName(JsonReader.getStringOrNull(jsonObject, "name"));

        return author;
    }

    @Override
    public JsonElement convertToJsonElement(Author author) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", author.getId());
        jsonObject.addProperty("name", author.getName());
        return jsonObject;
    }
    
}

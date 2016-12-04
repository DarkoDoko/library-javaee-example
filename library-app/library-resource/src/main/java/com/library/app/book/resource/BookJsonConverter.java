package com.library.app.book.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.json.EntityJsonConverter;
import com.library.app.json.JsonReader;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BookJsonConverter implements EntityJsonConverter<Book>{
    
    @Inject
    CategoryJsonConverter categoryJsonConverter;

    @Inject
    AuthorJsonConverter authorJsonConverter;
    
    @Override
    public Book convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);
        
        Book book = new Book();
        book.setTitle(JsonReader.getStringOrNull(jsonObject, "title"));
        book.setDescription(JsonReader.getStringOrNull(jsonObject, "description"));
        
        Category category = new Category();
        category.setId(JsonReader.getLongOrNull(jsonObject, "categoryId"));
        book.setCategory(category);
        
        JsonArray authorIds = jsonObject.getAsJsonArray("authorsIds");
        if(authorIds != null) {
            for(JsonElement authorId : authorIds){
                Author a = new Author();
                a.setId(authorId.getAsLong());
                book.addAuthor(a);
            }
        }
        
        book.setPrice(JsonReader.getDoubleOrNull(jsonObject, "price"));
        return book;        
    }

    @Override
    public JsonElement convertToJsonElement(Book book) {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("id", book.getId());
        jsonObject.addProperty("title", book.getTitle());
        jsonObject.addProperty("description", book.getDescription());
        jsonObject.add("category", categoryJsonConverter.convertToJsonElement(book.getCategory()));
        jsonObject.add("authors", authorJsonConverter.convertToJsonElement(book.getAuthors()));
        jsonObject.addProperty("price", book.getPrice());
        
        return jsonObject;        
    }
    
}

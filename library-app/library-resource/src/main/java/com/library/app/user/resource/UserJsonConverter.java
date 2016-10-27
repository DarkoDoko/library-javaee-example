package com.library.app.user.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.library.app.DateUtils;
import com.library.app.json.EntityJsonConverter;
import com.library.app.json.JsonReader;
import com.library.app.user.model.Customer;
import com.library.app.user.model.Employee;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserJsonConverter implements EntityJsonConverter<User> {

    @Override
    public User convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        User user = getUserInstance(jsonObject);
        user.setName(JsonReader.getStringOrNull(jsonObject, "name"));
        user.setEmail(JsonReader.getStringOrNull(jsonObject, "email"));
        user.setPassword(JsonReader.getStringOrNull(jsonObject, "password"));
        
        return user;
    }

    @Override
    public JsonElement convertToJsonElement(User user) {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("email", user.getEmail());
        jsonObject.addProperty("type", user.getUserType().toString());
        
        JsonArray roles = new JsonArray();
        user.getRoles().forEach((role) -> {
            roles.add(new JsonPrimitive(role.toString()));
        });
        
        jsonObject.add("roles", roles);
        jsonObject.addProperty("createdAt", DateUtils.fomatDateTime(user.getCreatedAt()));
        
        return jsonObject;
    }

    private User getUserInstance(final JsonObject userJson) {
        final UserType userType = UserType.valueOf(JsonReader.getStringOrNull(userJson, "type"));
        if (UserType.EMPLOYEE.equals(userType)) {
            return new Employee();
        }
        return new Customer();
    }

}

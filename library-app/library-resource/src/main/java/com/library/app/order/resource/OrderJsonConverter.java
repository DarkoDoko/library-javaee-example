package com.library.app.order.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.DateUtils;
import com.library.app.book.model.Book;
import com.library.app.json.EntityJsonConverter;
import com.library.app.json.JsonReader;
import com.library.app.order.model.Order;
import com.library.app.order.model.OrderHistoryEntry;
import com.library.app.order.model.OrderItem;
import com.library.app.user.model.User;
import java.util.List;

public class OrderJsonConverter implements EntityJsonConverter<Order>{

    @Override
    public Order convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);
        
        Order order = new Order();
        
        JsonArray itemsJsonArray = jsonObject.getAsJsonArray("items");
        if(itemsJsonArray != null) {
            itemsJsonArray.forEach((item) -> {
                Book book = new Book(JsonReader.getLongOrNull(item.getAsJsonObject(), "bookId"));
                Integer quantity = JsonReader.getIntegerOrNull(item.getAsJsonObject(), "quantity");
                order.addItem(book, quantity);
            });
        }
        
        return order;
    }

    @Override
    public JsonElement convertToJsonElement(Order order) {
        return getOrderAsJsonElement(order, true);
    }

    @Override
    public JsonElement convertToJsonElement(List<Order> orders) {
        JsonArray jsonArray = new JsonArray();
        
        orders.forEach((order) -> jsonArray.add(getOrderAsJsonElement(order, false)));
        
        return jsonArray;
    }

    private JsonElement getOrderAsJsonElement(Order order, boolean addItemsAndHistory) {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("id", order.getId());
        jsonObject.addProperty("createdAt", DateUtils.formatDateTime(order.getCreatedAt()));
        jsonObject.add("customer", getCustomerAsJsonElement(order.getCustomer()));
        
        if(addItemsAndHistory) {
            JsonArray jsonArrayItems = new JsonArray();
            order.getItems().forEach((orderItem) -> jsonArrayItems.add(getOrderItemAsJsonElement(orderItem)));   
            jsonObject.add("items", jsonArrayItems);
            
            JsonArray jsonArrayHistoryEntries = new JsonArray();
            order.getHistoryEntries().forEach(
                (historyEntry) -> jsonArrayHistoryEntries.add(getHistoryEntryAsJsonElement(historyEntry)));
            jsonObject.add("historyEntries", jsonArrayHistoryEntries);
        }
        
        jsonObject.addProperty("total", order.getTotal());
        jsonObject.addProperty("currentStatus", order.getCurrentStatus().name());
        
        return jsonObject;
        
    }

    private JsonElement getCustomerAsJsonElement(User user) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());
        
        return jsonObject;
    }

    private JsonElement getOrderItemAsJsonElement(OrderItem orderItem) {
        JsonObject jsonObject = new JsonObject();

		JsonObject jsonObjectBook = new JsonObject();
		jsonObjectBook.addProperty("id", orderItem.getBook().getId());
		jsonObjectBook.addProperty("title", orderItem.getBook().getTitle());

		jsonObject.add("book", jsonObjectBook);
		jsonObject.addProperty("quantity", orderItem.getQuantity());
		jsonObject.addProperty("price", orderItem.getPrice());

		return jsonObject;
    }

    private JsonElement getHistoryEntryAsJsonElement(OrderHistoryEntry historyEntry) {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("createdAt", DateUtils.formatDateTime(historyEntry.getCreatedAt()));
        jsonObject.addProperty("status", historyEntry.getStatus().name());
        
        return jsonObject;
    }
    
}

package com.library.app.logaudit.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.DateUtils;
import com.library.app.json.EntityJsonConverter;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.user.model.User;

public class LogAuditJsonConverter implements EntityJsonConverter<LogAudit>{

    @Override
    public LogAudit convertFrom(String json) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JsonElement convertToJsonElement(LogAudit logAudit) {
        JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", logAudit.getId());
		jsonObject.addProperty("createdAt", DateUtils.formatDateTime(logAudit.getCreatedAt()));
		jsonObject.add("user", getUserAsJsonElement(logAudit.getUser()));
		jsonObject.addProperty("action", logAudit.getAction().toString());
		jsonObject.addProperty("element", logAudit.getElement());

		return jsonObject;
        
    }

    private JsonElement getUserAsJsonElement(User user) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getId());
		jsonObject.addProperty("name", user.getName());

		return jsonObject;
    }
    
}

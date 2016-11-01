package com.library.app.commontests.utils;

import static com.library.app.commontests.utils.JsonTestUtils.*;
import com.library.app.user.model.User;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResourceClient {
    private URL urlBase;
    private String resourcePath;
    private User user;

    public ResourceClient(URL urlBase) {
        this.urlBase = urlBase;
    }

    public ResourceClient resourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        return this;
    }
    
    public ResourceClient user(User user) {
        this.user = user;
        return this;
    }
    
    public Response postWithFile(String fileName) {
        return postWithContent(getRequestFromFileOrEmptyIfNullFile(fileName));
    }

    public Response postWithContent(String content) {
        return buildClient().post(Entity.entity(content, MediaType.APPLICATION_JSON));
    }

    public Response putWithFile(String fileName) {
        return putWithContent(getRequestFromFileOrEmptyIfNullFile(fileName));
    }

    public Response putWithContent(String content) {
        return buildClient().put(Entity.entity(content, MediaType.APPLICATION_JSON));
    }

    public void delete() {
        buildClient().delete();
    }

    public Response get() {
            return buildClient().get();
    }

    private Builder buildClient() {
        Client resourceClient = ClientBuilder.newClient();
        if(user != null) {
            resourceClient = resourceClient.register(
                new HttpBasicAuthenticator(user.getEmail(), user.getPassword()));
        }
        return resourceClient.target(getFullURL(resourcePath)).request();
    }

    private String getFullURL(final String resourcePath) {
        try {
            return this.urlBase.toURI() + "api/" + resourcePath;
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getRequestFromFileOrEmptyIfNullFile(final String fileName) {
        if (fileName == null) {
            return "";
        }
        return readJsonFile(fileName);
    }
}
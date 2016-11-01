package com.library.app.commontests.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

public class HttpBasicAuthenticator implements ClientRequestFilter{
    
    private String user;
    private String password;

    public HttpBasicAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", getBasicAuthentication());        
    }

    private String getBasicAuthentication() {
        String userAndPassword = this.user + ":" + this.password;
        try {
            return "Basic " + Base64.getMimeEncoder().encodeToString(userAndPassword.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Error while converting using UTF-8", ex);
        }
    }
    
}

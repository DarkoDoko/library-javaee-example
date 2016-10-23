package com.library.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PasswordUtils {

    private PasswordUtils() {
    }
    
    public static String encryptPassword(String password) {
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
        
        md.update(password.getBytes());
        return Base64.getMimeEncoder().encodeToString(md.digest());
        
    }
}

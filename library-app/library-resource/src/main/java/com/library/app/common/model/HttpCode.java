package com.library.app.common.model;

/**
 *
 * @author ddoko
 */
public enum HttpCode {
    CREATED(201);
    
    private int code;
    
    private HttpCode(int code){
        this.code = code;
    }
    
    public int getCode(){
        return code;
    }
    
}
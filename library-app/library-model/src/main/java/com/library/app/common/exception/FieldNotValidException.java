package com.library.app.common.exception;

/**
 *
 * @author ddoko
 */
public class FieldNotValidException extends RuntimeException{
    private static final long serialVersionUID = 4525821332583716666L;
    
    String fieldName;

    public FieldNotValidException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "FieldNotValidException{" + "fieldName=" + fieldName + '}';
    }
    
}

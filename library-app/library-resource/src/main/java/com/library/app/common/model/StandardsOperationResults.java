package com.library.app.common.model;

import com.library.app.common.exception.FieldNotValidException;

/**
 *
 * @author ddoko
 */
public class StandardsOperationResults {

    private StandardsOperationResults() {
    }

    public static OperationResult getOperationResultExistent(ResourceMessage message, String fieldNames){
        return OperationResult.error(message.getKeyOfResourceExistent(), message.getMessageOfResourceExistent(fieldNames));
    }
    
    public static OperationResult getOperationResultInvalidField(ResourceMessage message, FieldNotValidException ex){
        return OperationResult.error(message.getKeyOfInvalidField(ex.getFieldName()), ex.getMessage());
    }
    
    public static OperationResult getOperationResultNotFound(ResourceMessage message){
        return OperationResult.error(message.getKeyOfResourceNotFound(), message.getMessageOfResourceNotFound());
    }
}

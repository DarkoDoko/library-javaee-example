package com.library.app.common.model;

import com.library.app.FieldNotValidException;

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
    
    public static OperationResult getOperationResultDependencyNotFound(ResourceMessage message, String dependecyField) {
        return OperationResult.error(message.getKeyOfInvalidField(dependecyField), message.getMessageNotFound());
    }
}

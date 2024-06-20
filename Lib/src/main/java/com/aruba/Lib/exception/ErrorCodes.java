package com.aruba.Lib.exception;


import lombok.Getter;

@Getter
public enum ErrorCodes {
	
    UNAUTHORIZED("Unauthorized. '{{originalMessage}}'"), 
    FORBIDDEN("Forbidden. '{{originalMessage}}'"), 
    INTERNAL_SERVER_ERROR("Internal Server Error. '{{originalMessage}}'"), 
    ILLEGAL_ARGUMENT_EXCEPTION("Illegal argument exception. '{{originalMessage}}'"), 
    DATA_INTEGRITY_VIOLATION_EXCEPTION("Data integrity violation exception. '{{originalMessage}}'"), 
    CANNOT_ACQUIRE_LOCK_EXCEPTION("Could not execute statement. '{{originalMessage}}'"), 
    ENTITY_DELETE_EXCEPTION("Cannot delete entity with id '{{id}}'."), 
    ENTITY_NOT_FOUND("Entity with id '{{id}}' was not found."), 
    VALIDATION_EXCEPTION("Validation Exception. '{{originalMessage}}'"), 
    ENTITY_ALREADY_EXISTS("Operation Not Permitted"), 
    VALIDATION_ERROR("Validation Error"); 

    private final String messageTemplate;

    ErrorCodes(String messageTemplate) {
	this.messageTemplate = messageTemplate;
    }

}
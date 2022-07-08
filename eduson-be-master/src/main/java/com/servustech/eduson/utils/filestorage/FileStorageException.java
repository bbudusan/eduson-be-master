package com.servustech.eduson.utils.filestorage;


import com.servustech.eduson.exceptions.LocalizableRuntimeException;

public class FileStorageException extends LocalizableRuntimeException {

	private static final long serialVersionUID = 1L;

	private Class entityClass;

    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, String messageKey, Object... messageArguments) {
        super(message, messageKey, messageArguments);
    }

    public FileStorageException(Class entityClass, String message) {
        super(message);
        this.entityClass = entityClass;
    }

    public Class getEntityClass() {
        return entityClass;
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

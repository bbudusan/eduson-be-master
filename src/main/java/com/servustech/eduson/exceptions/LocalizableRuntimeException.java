package com.servustech.eduson.exceptions;

public class LocalizableRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String messageKey;
    private Object[] messageArguments;
    
    public LocalizableRuntimeException(String message) {
        super(message);
    }

    public LocalizableRuntimeException(String messageKey, Object... messageArguments) {
        super();
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public LocalizableRuntimeException(String message, String messageKey, Object... messageArguments) {
        super(message);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public LocalizableRuntimeException(String message, Throwable cause, String messageKey, Object... messageArguments) {
        super(message, cause);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public LocalizableRuntimeException(Throwable cause, String messageKey, Object... messageArguments) {
        super(cause);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public LocalizableRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String messageKey, Object... messageArguments) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageArguments() {
        return messageArguments;
    }
}

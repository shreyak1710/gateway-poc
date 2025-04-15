
package com.zapcom.exception;

public class ApiGatewayException extends RuntimeException {
    
    private final String errorCode;
    
    public ApiGatewayException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ApiGatewayException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

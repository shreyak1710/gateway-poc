
package com.zapcom.utils;

public class Constants {
    // Header names
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String TRACKING_ID_HEADER = "X-Tracking-Id";
    
    // Service names
    public static final String AUTH_SERVICE = "AUTH-SERVICE";
    public static final String CUSTOMER_SERVICE = "CUSTOMER-SERVICE";
    
    // Paths
    public static final String API_PREFIX = "/api";
    public static final String AUTH_PATH = API_PREFIX + "/auth";
    public static final String CUSTOMER_PATH = API_PREFIX + "/customers";
    
    // Error codes
    public static final String ERR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERR_FORBIDDEN = "FORBIDDEN";
    public static final String ERR_BAD_REQUEST = "BAD_REQUEST";
    public static final String ERR_NOT_FOUND = "NOT_FOUND";
    public static final String ERR_INTERNAL_SERVER = "INTERNAL_SERVER_ERROR";
}

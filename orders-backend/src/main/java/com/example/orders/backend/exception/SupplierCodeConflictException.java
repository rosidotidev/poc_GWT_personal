package com.example.orders.backend.exception;

public class SupplierCodeConflictException extends RuntimeException {

    private final String code;

    public SupplierCodeConflictException(String code) {
        super("Supplier code already exists: " + code);
        this.code = code;
    }

    public SupplierCodeConflictException(String code, Throwable cause) {
        super("Supplier code already exists: " + code, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
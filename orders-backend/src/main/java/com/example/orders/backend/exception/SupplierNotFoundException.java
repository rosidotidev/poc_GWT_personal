package com.example.orders.backend.exception;

public class SupplierNotFoundException extends RuntimeException {

    public SupplierNotFoundException(Long id) {
        super("Supplier not found: " + id);
    }
}
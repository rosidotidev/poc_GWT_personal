package com.example.orders.frontend.server.bff.supplier;

public record SupplierRepresentation(
        long id,
        String code,
        String name,
        String contactName,
        String email,
        String phone,
        boolean active) {
}
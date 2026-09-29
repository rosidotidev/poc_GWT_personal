package com.example.orders.frontend.server.bff.supplier;

public record SupplierWriteRequest(
        String code,
        String name,
        String contactName,
        String email,
        String phone) {
}
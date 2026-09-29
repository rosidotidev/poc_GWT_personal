package com.example.orders.frontend.server.bff.supplier;

import java.util.Map;

public record SupplierProblem(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String code,
        Map<String, String> errors) {
}
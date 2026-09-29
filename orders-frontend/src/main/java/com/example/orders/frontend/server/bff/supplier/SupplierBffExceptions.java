package com.example.orders.frontend.server.bff.supplier;

import java.util.Map;

final class SupplierBffExceptions {

    private SupplierBffExceptions() {
    }

    static final class BadRequest extends RuntimeException {
        BadRequest(String message) { super(message); }
    }

    static final class InvalidInput extends RuntimeException {
        private final Map<String, String> errors;

        InvalidInput(Map<String, String> errors) {
            super("Supplier data is invalid");
            this.errors = Map.copyOf(errors);
        }

        Map<String, String> getErrors() { return errors; }
    }

    static final class NotFound extends RuntimeException {
        NotFound(long id) { super("Supplier " + id + " was not found"); }
    }

    static final class Conflict extends RuntimeException {
        Conflict(String code) { super("Supplier code already exists: " + code); }
    }

    static final class DownstreamUnavailable extends RuntimeException {
        DownstreamUnavailable(Throwable cause) { super("Supplier service is unavailable", cause); }
    }
}
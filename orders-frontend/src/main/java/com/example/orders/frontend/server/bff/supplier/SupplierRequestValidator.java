package com.example.orders.frontend.server.bff.supplier;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SupplierRequestValidator {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public void validate(SupplierWriteRequest request) {
        if (request == null) {
            throw new SupplierBffExceptions.BadRequest("A JSON supplier object is required");
        }

        Map<String, String> errors = new LinkedHashMap<>();
        required(errors, "code", request.code(), 40);
        required(errors, "name", request.name(), 160);
        optionalMax(errors, "contactName", request.contactName(), 160);
        optionalMax(errors, "email", request.email(), 200);
        optionalMax(errors, "phone", request.phone(), 60);
        if (request.email() != null && !request.email().isBlank()
                && !EMAIL.matcher(request.email().trim()).matches()) {
            errors.put("email", "must be a valid email address");
        }
        if (!errors.isEmpty()) throw new SupplierBffExceptions.InvalidInput(errors);
    }

    private void required(Map<String, String> errors, String field, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            errors.put(field, "is required");
        } else if (value.trim().length() > maxLength) {
            errors.put(field, "must be at most " + maxLength + " characters");
        }
    }

    private void optionalMax(Map<String, String> errors, String field, String value, int maxLength) {
        if (value != null && value.trim().length() > maxLength) {
            errors.put(field, "must be at most " + maxLength + " characters");
        }
    }
}
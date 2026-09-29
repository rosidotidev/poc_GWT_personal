package com.example.orders.backend.repository;

import com.example.orders.backend.model.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSupplierRepository implements SupplierRepository {

    private static final String SELECT_COLUMNS = "id, code, name, contact_name, email, phone, active";
    private final JdbcTemplate jdbc;

    public JdbcSupplierRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Supplier> findAll() {
        return jdbc.query("SELECT " + SELECT_COLUMNS + " FROM suppliers ORDER BY name", this::mapSupplier);
    }

    @Override
    public List<Supplier> searchActive(String query) {
        if ("**".equals(query)) {
            return jdbc.query("SELECT " + SELECT_COLUMNS
                    + " FROM suppliers WHERE active = TRUE ORDER BY name LIMIT 20", this::mapSupplier);
        }
        String pattern = "%" + query.toLowerCase() + "%";
        return jdbc.query("SELECT " + SELECT_COLUMNS
                + " FROM suppliers WHERE active = TRUE AND (LOWER(name) LIKE ? OR LOWER(code) LIKE ?) "
                + "ORDER BY name LIMIT 20", this::mapSupplier, pattern, pattern);
    }

    @Override
    public Optional<Supplier> findById(long id) {
        return jdbc.query("SELECT " + SELECT_COLUMNS + " FROM suppliers WHERE id = ?", this::mapSupplier, id)
                .stream().findFirst();
    }

    @Override
    public Optional<Supplier> findByCode(String code) {
        return jdbc.query("SELECT " + SELECT_COLUMNS + " FROM suppliers WHERE code = ?", this::mapSupplier, code)
                .stream().findFirst();
    }

    @Override
    public Supplier save(Supplier supplier) {
        if (supplier.getId() == null) {
            KeyHolder key = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO suppliers (code, name, contact_name, email, phone, active) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, supplier.getCode());
                statement.setString(2, supplier.getName());
                statement.setString(3, supplier.getContactName());
                statement.setString(4, supplier.getEmail());
                statement.setString(5, supplier.getPhone());
                statement.setBoolean(6, supplier.isActive());
                return statement;
            }, key);
            Number generatedId = key.getKey();
            if (generatedId == null) {
                throw new IllegalStateException("H2 did not return the generated supplier ID");
            }
            supplier.setId(generatedId.longValue());
        } else {
            jdbc.update("UPDATE suppliers SET code = ?, name = ?, contact_name = ?, email = ?, phone = ?, active = ? WHERE id = ?",
                    supplier.getCode(), supplier.getName(), supplier.getContactName(), supplier.getEmail(), supplier.getPhone(),
                    supplier.isActive(), supplier.getId());
        }
        return supplier;
    }

    private Supplier mapSupplier(java.sql.ResultSet row, int rowNumber) throws java.sql.SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(row.getLong("id"));
        supplier.setCode(row.getString("code"));
        supplier.setName(row.getString("name"));
        supplier.setContactName(row.getString("contact_name"));
        supplier.setEmail(row.getString("email"));
        supplier.setPhone(row.getString("phone"));
        supplier.setActive(row.getBoolean("active"));
        return supplier;
    }
}
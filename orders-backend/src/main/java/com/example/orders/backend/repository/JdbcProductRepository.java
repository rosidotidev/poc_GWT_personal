package com.example.orders.backend.repository;

import com.example.orders.backend.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private static final String SELECT_COLUMNS = "id, sku, name, description, unit_price, active";
    private final JdbcTemplate jdbc;

    public JdbcProductRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Product> findAll() {
        return withSupplierIds(jdbc.query("SELECT " + SELECT_COLUMNS + " FROM products ORDER BY name", this::mapProduct));
    }

    @Override
    public List<Product> findActive() {
        return withSupplierIds(jdbc.query("SELECT " + SELECT_COLUMNS + " FROM products WHERE active = TRUE ORDER BY name", this::mapProduct));
    }

    @Override
    public List<Product> searchActiveByName(String name) {
        String query = name == null ? "" : name.trim();
        if ("**".equals(query)) {
            return withSupplierIds(jdbc.query("SELECT " + SELECT_COLUMNS
                + " FROM products WHERE active = TRUE ORDER BY name LIMIT 20", this::mapProduct));
        }
        return withSupplierIds(jdbc.query("SELECT " + SELECT_COLUMNS
                + " FROM products WHERE active = TRUE AND LOWER(name) LIKE ? ORDER BY name LIMIT 10",
            this::mapProduct, "%" + query.toLowerCase() + "%"));
    }

    @Override
    public Optional<Product> findById(long id) {
        List<Product> rows = withSupplierIds(jdbc.query(
            "SELECT " + SELECT_COLUMNS + " FROM products WHERE id = ?", this::mapProduct, id));
        return rows.stream().findFirst();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            KeyHolder key = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO products (sku, name, description, unit_price, active) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, product.getSku());
                statement.setString(2, product.getName());
                statement.setString(3, product.getDescription());
                statement.setBigDecimal(4, product.getUnitPrice());
                statement.setBoolean(5, product.isActive());
                return statement;
            }, key);
            Number generatedId = key.getKey();
            if (generatedId == null) {
                throw new IllegalStateException("H2 did not return the generated product ID");
            }
            product.setId(generatedId.longValue());
        } else {
            jdbc.update("UPDATE products SET sku = ?, name = ?, description = ?, unit_price = ?, active = ? WHERE id = ?",
                    product.getSku(), product.getName(), product.getDescription(), product.getUnitPrice(),
                    product.isActive(), product.getId());
        }
        jdbc.update("DELETE FROM product_suppliers WHERE product_id = ?", product.getId());
        for (Long supplierId : product.getSupplierIds()) {
            jdbc.update("INSERT INTO product_suppliers (product_id, supplier_id) VALUES (?, ?)",
                    product.getId(), supplierId);
        }
        return product;
    }

    private List<Product> withSupplierIds(List<Product> products) {
        for (Product product : products) {
            product.setSupplierIds(jdbc.queryForList(
                    "SELECT supplier_id FROM product_suppliers WHERE product_id = ? ORDER BY supplier_id",
                    Long.class, product.getId()));
        }
        return products;
    }

    private Product mapProduct(java.sql.ResultSet row, int rowNumber) throws java.sql.SQLException {
        Product product = new Product();
        product.setId(row.getLong("id"));
        product.setSku(row.getString("sku"));
        product.setName(row.getString("name"));
        product.setDescription(row.getString("description"));
        product.setUnitPrice(row.getBigDecimal("unit_price"));
        product.setActive(row.getBoolean("active"));
        return product;
    }
}
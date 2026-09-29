package com.example.orders.backend.repository;

import com.example.orders.backend.model.Order;
import com.example.orders.backend.model.OrderItem;
import com.example.orders.backend.model.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbc;

    public JdbcOrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            KeyHolder key = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO orders (customer_name, description, status, created_at) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, order.getCustomerName());
                statement.setString(2, order.getDescription());
                statement.setString(3, order.getStatus().name());
                statement.setString(4, order.getCreatedAt().toString());
                return statement;
            }, key);
            Number generatedId = key.getKey();
            if (generatedId == null) {
                throw new IllegalStateException("H2 did not return the generated order ID");
            }
            order.setId(generatedId.longValue());
        } else {
            jdbc.update("UPDATE orders SET customer_name = ?, description = ?, status = ?, created_at = ? WHERE id = ?",
                    order.getCustomerName(), order.getDescription(), order.getStatus().name(),
                    order.getCreatedAt().toString(), order.getId());
            jdbc.update("DELETE FROM order_items WHERE order_id = ?", order.getId());
        }

        for (OrderItem item : order.getItems()) {
            jdbc.update("INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)",
                    order.getId(), item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice());
        }
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        List<Order> rows = jdbc.query("SELECT id, customer_name, description, status, created_at FROM orders WHERE id = ?",
                (row, rowNumber) -> mapOrder(row.getLong("id"), row.getString("customer_name"),
                        row.getString("description"), row.getString("status"), row.getString("created_at")), id);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Order order = rows.get(0);
        order.setItems(findItems(order.getId()));
        return Optional.of(order);
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = jdbc.query("SELECT id, customer_name, description, status, created_at FROM orders ORDER BY id DESC",
                (row, rowNumber) -> mapOrder(row.getLong("id"), row.getString("customer_name"),
                        row.getString("description"), row.getString("status"), row.getString("created_at")));
        for (Order order : orders) {
            order.setItems(findItems(order.getId()));
        }
        return orders;
    }

    private List<OrderItem> findItems(long orderId) {
        return jdbc.query("SELECT product_id, product_name, quantity, unit_price FROM order_items WHERE order_id = ? ORDER BY id",
                (row, rowNumber) -> {
                    OrderItem item = new OrderItem(row.getString("product_name"), row.getInt("quantity"),
                            row.getBigDecimal("unit_price"));
                    long productId = row.getLong("product_id");
                    item.setProductId(row.wasNull() ? null : productId);
                    return item;
                }, orderId);
    }

    private Order mapOrder(long id, String customerName, String description, String status, String createdAt) {
        Order order = new Order();
        order.setId(id);
        order.setCustomerName(customerName);
        order.setDescription(description);
        order.setStatus(OrderStatus.valueOf(status));
        order.setCreatedAt(Instant.parse(createdAt));
        order.setItems(new ArrayList<>());
        return order;
    }
}
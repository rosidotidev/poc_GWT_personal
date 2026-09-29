package com.example.orders.backend.service;

import com.example.orders.backend.exception.OrderNotFoundException;
import com.example.orders.backend.model.Order;
import com.example.orders.backend.model.OrderStatus;
import com.example.orders.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setId(null);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(Instant.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order approveOrder(Long id) {
        Order order = getOrder(id);
        requireStatus(order, OrderStatus.CREATED, "approved");
        order.setStatus(OrderStatus.APPROVED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order sendOrder(Long id) {
        Order order = getOrder(id);
        requireStatus(order, OrderStatus.APPROVED, "sent");
        order.setStatus(OrderStatus.SENT);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    private void requireStatus(Order order, OrderStatus expected, String action) {
        if (order.getStatus() != expected) {
            throw new IllegalStateException(
                    "Order " + order.getId() + " cannot be " + action + " from status " + order.getStatus());
        }
    }
}

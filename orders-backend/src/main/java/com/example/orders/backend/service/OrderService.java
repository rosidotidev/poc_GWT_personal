package com.example.orders.backend.service;

import com.example.orders.backend.model.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order);

    Order approveOrder(Long id);

    Order sendOrder(Long id);

    Order getOrder(Long id);

    List<Order> listOrders();
}

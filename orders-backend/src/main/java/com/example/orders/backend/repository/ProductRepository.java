package com.example.orders.backend.repository;

import com.example.orders.backend.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    List<Product> findActive();

    List<Product> searchActiveByName(String name);

    Optional<Product> findById(long id);

    Product save(Product product);
}
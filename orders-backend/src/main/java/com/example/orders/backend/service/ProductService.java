package com.example.orders.backend.service;

import com.example.orders.backend.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> listProducts();

    List<Product> listActiveProducts();

    List<Product> searchActiveProductsByName(String name);

    Product getProduct(long id);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    Product setProductActive(long id, boolean active);
}
package com.example.orders.backend.repository;

import com.example.orders.backend.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

    List<Supplier> findAll();

    List<Supplier> searchActive(String query);

    Optional<Supplier> findById(long id);

    Optional<Supplier> findByCode(String code);

    Supplier save(Supplier supplier);
}
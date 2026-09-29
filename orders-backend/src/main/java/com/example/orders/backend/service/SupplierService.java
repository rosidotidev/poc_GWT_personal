package com.example.orders.backend.service;

import com.example.orders.backend.model.Supplier;

import java.util.List;

public interface SupplierService {

    List<Supplier> listSuppliers();

    List<Supplier> searchActiveSuppliers(String query);

    Supplier createSupplier(Supplier supplier);

    Supplier updateSupplier(Supplier supplier);

    Supplier setSupplierActive(long id, boolean active);
}
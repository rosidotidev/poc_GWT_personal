package com.example.orders.backend.service;

import com.example.orders.backend.exception.SupplierNotFoundException;
import com.example.orders.backend.exception.SupplierCodeConflictException;
import com.example.orders.backend.model.Supplier;
import com.example.orders.backend.repository.SupplierRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> listSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> searchActiveSuppliers(String query) {
        String normalized = query == null ? "" : query.trim();
        if (!"**".equals(normalized) && normalized.length() < 2) {
            return List.of();
        }
        return supplierRepository.searchActive(normalized.toLowerCase());
    }

    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        validate(supplier);
        supplier.setId(null);
        normalize(supplier);
        supplier.setActive(true);
        ensureCodeAvailable(supplier.getCode(), null);
        return save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Supplier supplier) {
        if (supplier.getId() == null) {
            throw new IllegalArgumentException("Supplier ID is required for update");
        }
        Supplier existing = getSupplier(supplier.getId());
        validate(supplier);
        normalize(supplier);
        ensureCodeAvailable(supplier.getCode(), supplier.getId());
        supplier.setActive(existing.isActive());
        return save(supplier);
    }

    @Override
    @Transactional
    public Supplier setSupplierActive(long id, boolean active) {
        Supplier supplier = getSupplier(id);
        supplier.setActive(active);
        return supplierRepository.save(supplier);
    }

    private Supplier getSupplier(long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new SupplierNotFoundException(id));
    }

    private void validate(Supplier supplier) {
        if (supplier.getCode() == null || supplier.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier code is required");
        }
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name is required");
        }
    }

    private void normalize(Supplier supplier) {
        supplier.setCode(supplier.getCode().trim().toUpperCase());
        supplier.setName(supplier.getName().trim());
        supplier.setContactName(clean(supplier.getContactName()));
        supplier.setEmail(clean(supplier.getEmail()));
        supplier.setPhone(clean(supplier.getPhone()));
    }

    private void ensureCodeAvailable(String code, Long exceptId) {
        supplierRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(exceptId))
                .ifPresent(existing -> { throw new SupplierCodeConflictException(code); });
    }

    private Supplier save(Supplier supplier) {
        try {
            return supplierRepository.save(supplier);
        } catch (DuplicateKeyException e) {
            throw new SupplierCodeConflictException(supplier.getCode(), e);
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
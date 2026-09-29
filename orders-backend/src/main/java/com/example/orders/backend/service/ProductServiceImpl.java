package com.example.orders.backend.service;

import com.example.orders.backend.exception.ProductNotFoundException;
import com.example.orders.backend.model.Product;
import com.example.orders.backend.repository.ProductRepository;
import com.example.orders.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> listActiveProducts() {
        return productRepository.findActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchActiveProductsByName(String name) {
        String query = name == null ? "" : name.trim();
        if (!"**".equals(query) && query.length() < 3) {
            return List.of();
        }
        return productRepository.searchActiveByName(query);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        validate(product);
        product.setId(null);
        product.setSku(product.getSku().trim().toUpperCase());
        product.setName(product.getName().trim());
        product.setDescription(product.getDescription() == null ? "" : product.getDescription().trim());
        product.setActive(true);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID is required for update");
        }
        Product existing = getProduct(product.getId());
        validate(product);
        product.setSku(product.getSku().trim().toUpperCase());
        product.setName(product.getName().trim());
        product.setDescription(product.getDescription() == null ? "" : product.getDescription().trim());
        product.setActive(existing.isActive());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product setProductActive(long id, boolean active) {
        Product product = getProduct(id);
        product.setActive(active);
        return productRepository.save(product);
    }

    private void validate(Product product) {
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU is required");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().signum() < 0) {
            throw new IllegalArgumentException("Product price must be zero or greater");
        }
        for (Long supplierId : product.getSupplierIds()) {
            if (supplierId == null || supplierRepository.findById(supplierId).isEmpty()) {
                throw new IllegalArgumentException("Unknown supplier: " + supplierId);
            }
        }
    }
}
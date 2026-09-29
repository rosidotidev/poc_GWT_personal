package com.example.orders.backend.soap;

import com.example.orders.backend.model.Product;
import com.example.orders.contract.ProductSoapDto;

final class ProductSoapMapper {

    private ProductSoapMapper() {
    }

    static Product toProduct(ProductSoapDto dto) {
        Product product = new Product();
        product.setId(dto.getId() == 0 ? null : dto.getId());
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setUnitPrice(dto.getUnitPrice());
        product.setActive(dto.isActive());
        product.setSupplierIds(dto.getSupplierIds());
        return product;
    }

    static ProductSoapDto toSoapDto(Product product) {
        ProductSoapDto dto = new ProductSoapDto();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setActive(product.isActive());
        dto.setSupplierIds(product.getSupplierIds());
        return dto;
    }
}
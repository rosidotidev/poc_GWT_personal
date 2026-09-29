package com.example.orders.backend.soap;

import com.example.orders.backend.model.Supplier;
import com.example.orders.contract.SupplierSoapDto;

final class SupplierSoapMapper {

    private SupplierSoapMapper() {
    }

    static Supplier toSupplier(SupplierSoapDto dto) {
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId() == 0 ? null : dto.getId());
        supplier.setCode(dto.getCode());
        supplier.setName(dto.getName());
        supplier.setContactName(dto.getContactName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setActive(dto.isActive());
        return supplier;
    }

    static SupplierSoapDto toSoapDto(Supplier supplier) {
        SupplierSoapDto dto = new SupplierSoapDto();
        dto.setId(supplier.getId());
        dto.setCode(supplier.getCode());
        dto.setName(supplier.getName());
        dto.setContactName(supplier.getContactName());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setActive(supplier.isActive());
        return dto;
    }
}
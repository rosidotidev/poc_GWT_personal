package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Product", namespace = OrdersSoapService.NAMESPACE,
        propOrder = {"id", "sku", "name", "description", "unitPrice", "active", "supplierIds"})
public class ProductSoapDto {

    private long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private boolean active;
    private List<Long> supplierIds = new ArrayList<>();

    public ProductSoapDto() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Long> getSupplierIds() { return supplierIds; }
    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds == null ? new ArrayList<>() : new ArrayList<>(supplierIds);
    }
}
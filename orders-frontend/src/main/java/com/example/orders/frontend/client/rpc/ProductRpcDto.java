package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class ProductRpcDto implements IsSerializable {

    private long id;
    private String sku;
    private String name;
    private String description;
    private double unitPrice;
    private boolean active;
    private List<Long> supplierIds = new ArrayList<>();

    public ProductRpcDto() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Long> getSupplierIds() { return supplierIds; }
    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds == null ? new ArrayList<>() : new ArrayList<>(supplierIds);
    }
}
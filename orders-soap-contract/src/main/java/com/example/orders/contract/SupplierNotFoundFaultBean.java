package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupplierNotFoundFault", namespace = OrdersSoapService.NAMESPACE)
public class SupplierNotFoundFaultBean {

    private long supplierId;
    private String message;

    public SupplierNotFoundFaultBean() {
    }

    public SupplierNotFoundFaultBean(long supplierId, String message) {
        this.supplierId = supplierId;
        this.message = message;
    }

    public long getSupplierId() { return supplierId; }
    public void setSupplierId(long supplierId) { this.supplierId = supplierId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
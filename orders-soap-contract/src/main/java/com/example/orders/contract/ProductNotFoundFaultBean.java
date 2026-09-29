package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductNotFoundFault", namespace = OrdersSoapService.NAMESPACE)
public class ProductNotFoundFaultBean {

    private long productId;
    private String message;

    public ProductNotFoundFaultBean() {
    }

    public ProductNotFoundFaultBean(long productId, String message) {
        this.productId = productId;
        this.message = message;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
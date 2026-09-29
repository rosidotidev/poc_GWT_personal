package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderNotFoundFault", namespace = OrdersSoapService.NAMESPACE)
public class OrderNotFoundFaultBean {

    private long orderId;
    private String message;

    public OrderNotFoundFaultBean() {
    }

    public OrderNotFoundFaultBean(long orderId, String message) {
        this.orderId = orderId;
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
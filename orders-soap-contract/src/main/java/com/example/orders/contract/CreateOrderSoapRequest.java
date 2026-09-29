package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateOrderRequest", namespace = OrdersSoapService.NAMESPACE,
        propOrder = {"customerName", "description", "items"})
public class CreateOrderSoapRequest {

    private String customerName;
    private String description;
    private List<OrderItemSoapDto> items = new ArrayList<>();

    public CreateOrderSoapRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrderItemSoapDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemSoapDto> items) {
        this.items = items;
    }
}
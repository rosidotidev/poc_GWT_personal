package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order", namespace = OrdersSoapService.NAMESPACE,
        propOrder = {"id", "customerName", "description", "items", "status", "createdAt"})
public class OrderSoapDto {

    private long id;
    private String customerName;
    private String description;
    private List<OrderItemSoapDto> items = new ArrayList<>();
    private String status;
    private XMLGregorianCalendar createdAt;

    public OrderSoapDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public XMLGregorianCalendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(XMLGregorianCalendar createdAt) {
        this.createdAt = createdAt;
    }
}
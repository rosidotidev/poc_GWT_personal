package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderRpcRequest implements IsSerializable {

    private String customerName;
    private String description;
    private List<OrderItemRpcDto> items = new ArrayList<>();

    public CreateOrderRpcRequest() {
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

    public List<OrderItemRpcDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRpcDto> items) {
        this.items = items;
    }
}
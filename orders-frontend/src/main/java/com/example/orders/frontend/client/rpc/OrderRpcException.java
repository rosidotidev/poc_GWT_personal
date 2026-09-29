package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OrderRpcException extends Exception implements IsSerializable {

    public OrderRpcException() {
    }

    public OrderRpcException(String message) {
        super(message);
    }
}
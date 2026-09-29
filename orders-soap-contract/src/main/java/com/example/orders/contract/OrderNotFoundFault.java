package com.example.orders.contract;

import jakarta.xml.ws.WebFault;

@WebFault(name = "OrderNotFoundFault", targetNamespace = OrdersSoapService.NAMESPACE,
        faultBean = "com.example.orders.contract.OrderNotFoundFaultBean")
public class OrderNotFoundFault extends Exception {

    private final OrderNotFoundFaultBean faultInfo;

    public OrderNotFoundFault(String message, OrderNotFoundFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public OrderNotFoundFault(String message, OrderNotFoundFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public OrderNotFoundFaultBean getFaultInfo() {
        return faultInfo;
    }
}
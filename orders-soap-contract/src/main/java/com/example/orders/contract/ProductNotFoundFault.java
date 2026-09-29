package com.example.orders.contract;

import jakarta.xml.ws.WebFault;

@WebFault(name = "ProductNotFoundFault", targetNamespace = OrdersSoapService.NAMESPACE,
        faultBean = "com.example.orders.contract.ProductNotFoundFaultBean")
public class ProductNotFoundFault extends Exception {

    private final ProductNotFoundFaultBean faultInfo;

    public ProductNotFoundFault(String message, ProductNotFoundFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public ProductNotFoundFault(String message, ProductNotFoundFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public ProductNotFoundFaultBean getFaultInfo() { return faultInfo; }
}
package com.example.orders.contract;

import jakarta.xml.ws.WebFault;

@WebFault(name = "SupplierNotFoundFault", targetNamespace = OrdersSoapService.NAMESPACE,
        faultBean = "com.example.orders.contract.SupplierNotFoundFaultBean")
public class SupplierNotFoundFault extends Exception {

    private final SupplierNotFoundFaultBean faultInfo;

    public SupplierNotFoundFault(String message, SupplierNotFoundFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public SupplierNotFoundFault(String message, SupplierNotFoundFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public SupplierNotFoundFaultBean getFaultInfo() { return faultInfo; }
}
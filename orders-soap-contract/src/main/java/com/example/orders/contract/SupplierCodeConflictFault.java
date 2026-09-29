package com.example.orders.contract;

import jakarta.xml.ws.WebFault;

@WebFault(name = "SupplierCodeConflictFault", targetNamespace = OrdersSoapService.NAMESPACE,
        faultBean = "com.example.orders.contract.SupplierCodeConflictFaultBean")
public class SupplierCodeConflictFault extends Exception {

    private final SupplierCodeConflictFaultBean faultInfo;

    public SupplierCodeConflictFault(String message, SupplierCodeConflictFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public SupplierCodeConflictFault(String message, SupplierCodeConflictFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public SupplierCodeConflictFaultBean getFaultInfo() {
        return faultInfo;
    }
}
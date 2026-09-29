package com.example.orders.contract;

import jakarta.xml.ws.WebFault;

@WebFault(name = "InvalidOrderStateFault", targetNamespace = OrdersSoapService.NAMESPACE,
        faultBean = "com.example.orders.contract.InvalidOrderStateFaultBean")
public class InvalidOrderStateFault extends Exception {

    private final InvalidOrderStateFaultBean faultInfo;

    public InvalidOrderStateFault(String message, InvalidOrderStateFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public InvalidOrderStateFault(String message, InvalidOrderStateFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public InvalidOrderStateFaultBean getFaultInfo() {
        return faultInfo;
    }
}
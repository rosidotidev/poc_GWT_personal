package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupplierCodeConflictFault", namespace = OrdersSoapService.NAMESPACE)
public class SupplierCodeConflictFaultBean {

    private String code;
    private String message;

    public SupplierCodeConflictFaultBean() {
    }

    public SupplierCodeConflictFaultBean(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
package com.example.orders.contract;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidOrderStateFault", namespace = OrdersSoapService.NAMESPACE)
public class InvalidOrderStateFaultBean {

    private long orderId;
    private String currentStatus;
    private String attemptedAction;
    private String message;

    public InvalidOrderStateFaultBean() {
    }

    public InvalidOrderStateFaultBean(long orderId, String currentStatus, String attemptedAction, String message) {
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.attemptedAction = attemptedAction;
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }

    public void setAttemptedAction(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
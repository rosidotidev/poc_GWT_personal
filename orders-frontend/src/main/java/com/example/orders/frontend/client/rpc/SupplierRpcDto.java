package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SupplierRpcDto implements IsSerializable {

    private long id;
    private String code;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private boolean active;

    public SupplierRpcDto() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
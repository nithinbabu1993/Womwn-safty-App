package com.example.womensaftyapp;

public class UserModel {
    String pin, name, phone, utype, parentId;

    public UserModel(String pin, String name, String phone, String utype, String parentId) {
        this.pin = pin;
        this.name = name;
        this.phone = phone;
        this.utype = utype;
        this.parentId = parentId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
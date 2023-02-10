package com.example.womensaftyapp;

public class UserModel {
    String pin,name,phone,utype;

    public UserModel(String pin, String name, String phone, String utype) {
        this.pin = pin;
        this.name = name;
        this.phone = phone;
        this.utype = utype;
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
}

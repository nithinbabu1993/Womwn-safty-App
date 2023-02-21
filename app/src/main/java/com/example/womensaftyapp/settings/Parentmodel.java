package com.example.womensaftyapp.settings;

public class Parentmodel {
    String name,phone;

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

    public Parentmodel(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}

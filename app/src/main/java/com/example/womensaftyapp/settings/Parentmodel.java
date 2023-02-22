package com.example.womensaftyapp.settings;

public class Parentmodel {
    String uid,name,phone;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Parentmodel(String uId,String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.uid = uId;
    }
}

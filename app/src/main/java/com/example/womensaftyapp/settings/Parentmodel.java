package com.example.womensaftyapp.settings;

public class Parentmodel {
    String uid,name,phone,parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Parentmodel(String uid, String name, String phone, String parentId) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.parentId = parentId;
    }

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


}

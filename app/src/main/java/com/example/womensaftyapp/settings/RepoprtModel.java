package com.example.womensaftyapp.settings;

public class RepoprtModel {
    String rid,uid,issue,rdate,uname,uphone;

    public RepoprtModel(String rid, String uid, String issue, String rdate, String uname, String uphone) {
        this.rid = rid;
        this.uid = uid;
        this.issue = issue;
        this.rdate = rdate;
        this.uname = uname;
        this.uphone = uphone;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }
}

package com.example.womensaftyapp.settings;

public class RepoprtModel {
    String rid,uid,issue,rdate,uname,uphone,utype,liveLatitude,liveLongitude;

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

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getLiveLatitude() {
        return liveLatitude;
    }

    public void setLiveLatitude(String liveLatitude) {
        this.liveLatitude = liveLatitude;
    }

    public String getLiveLongitude() {
        return liveLongitude;
    }

    public void setLiveLongitude(String liveLongitude) {
        this.liveLongitude = liveLongitude;
    }

    public RepoprtModel(String rid, String uid, String issue, String rdate, String uname, String uphone, String utype, String liveLatitude, String liveLongitude) {
        this.rid = rid;
        this.uid = uid;
        this.issue = issue;
        this.rdate = rdate;
        this.uname = uname;
        this.uphone = uphone;
        this.utype = utype;
        this.liveLatitude = liveLatitude;
        this.liveLongitude = liveLongitude;
    }
}

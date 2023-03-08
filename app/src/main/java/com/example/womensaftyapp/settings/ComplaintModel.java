package com.example.womensaftyapp.settings;

public class ComplaintModel {
    String uname,uphone,complaint,stationId,complaintDate,stationName,stationPhone,uid;

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

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(String complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationPhone() {
        return stationPhone;
    }

    public void setStationPhone(String stationPhone) {
        this.stationPhone = stationPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ComplaintModel(String uname, String uphone, String complaint, String stationId, String complaintDate, String stationName, String stationPhone, String uid) {
        this.uname = uname;
        this.uphone = uphone;
        this.complaint = complaint;
        this.stationId = stationId;
        this.complaintDate = complaintDate;
        this.stationName = stationName;
        this.stationPhone = stationPhone;
        this.uid = uid;
    }
}

package com.rudraksha.rudrakshashakti.Pojo;

public class UpcommingCalls {
    String userProfilePic,userName,callTime,service,callDate,callDuration;

    public UpcommingCalls(String userProfilePic, String userName, String callTime, String service, String callDate, String callDuration) {
        this.userProfilePic = userProfilePic;
        this.userName = userName;
        this.callTime = callTime;
        this.service = service;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    public UpcommingCalls(){}


    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }
}

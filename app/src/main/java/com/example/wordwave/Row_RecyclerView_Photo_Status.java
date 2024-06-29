package com.example.wordwave;

public class Row_RecyclerView_Photo_Status {
    String timeStemp, photostatusUri, userName;

    Row_RecyclerView_Photo_Status(String timeStemp, String photostatusUri, String userName) {
        this.timeStemp = timeStemp;
        this.photostatusUri = photostatusUri;
        this.userName = userName;
    }

    Row_RecyclerView_Photo_Status() {

    }

    public String getTimeStemp() {
        return timeStemp;
    }

    public void setTimeStemp(String timeStemp) {
        this.timeStemp = timeStemp;
    }

    public String getPhotostatusUri() {
        return photostatusUri;
    }

    public void setPhotostatusUri(String photostatusUri) {
        this.photostatusUri = photostatusUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

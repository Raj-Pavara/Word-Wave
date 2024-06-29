package com.example.wordwave;

public class MessageModel {
    String content, senderId;
    long timeStamp;

    MessageModel(String content, String senderId, long timeStamp) {
        this.content = content;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public MessageModel() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

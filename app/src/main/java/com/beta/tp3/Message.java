package com.beta.tp3;

public class Message {
    private String sendDate, fromName, message;
    private boolean isSelf;

    public Message() {
    }

    public Message(String sendDate, String fromName, String message, boolean isSelf) {
        this.sendDate = sendDate;
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getDateSend() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }
}

package com.example.mychatapp.Model;

public class Message {
    private String idSender;
    private String idReceiver;
    private String msg;

    public Message(String idSender, String idReceiver, String msg) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.msg = msg;
    }

    public Message(){

    }
    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

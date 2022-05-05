package com.example.entityclass;

public class Message {
    private String img;
    private String msg;
    private String type;

    public Message(String img, String msg, String type) {
        this.img = img;
        this.msg = msg;
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

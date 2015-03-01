package com.ar.ipsum.ipsumapp;

/**
 * Created by QSR on 27-02-2015.
 */
public class LoginReply {
    private String status;
    private String token;


    public LoginReply() {
        this.status = "";
        this.token = "";
    }

    public LoginReply(String status, String token) {
        this.status = status;
        this.token = token;
    }

    public String getStatus(){
        return status;
    }

    public String gettoken(){
        return token;
    }
}

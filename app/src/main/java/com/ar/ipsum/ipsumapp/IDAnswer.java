package com.ar.ipsum.ipsumapp;

/**
 * Created by tiago_000 on 21/03/2015.
 */
public class IDAnswer {
    private String email;
    private String id;


    public IDAnswer() {
        this.email = "";
        this.id = "";
    }

    public IDAnswer(String email, String id) {
        this.email = email;
        this.id = id;
    }

    public String getEmail(){
        return email;
    }

    public String getId(){
        return id;
    }
}

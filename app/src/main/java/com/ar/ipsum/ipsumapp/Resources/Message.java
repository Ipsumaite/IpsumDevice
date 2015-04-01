package com.ar.ipsum.ipsumapp.Resources;

/**
 * Created by tiago_000 on 28/03/2015.
 */
public class Message {
    private String channel;
    private String content;
    private String date;
    private float latitude;
    private float longitude;

    public Message (){
        this.channel= "";
        this.content= "";
        this.date= "";
        this.latitude= 0;
        this.longitude= 0;


    }

    public Message (String channel, String content, String date, float latitude, float longitude){
        this.channel= channel;
        this.content= content;
        this.date= date;
        this.latitude= latitude;
        this.longitude= longitude;


    }


}

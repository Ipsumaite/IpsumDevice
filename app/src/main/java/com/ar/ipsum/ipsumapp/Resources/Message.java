package com.ar.ipsum.ipsumapp.Resources;

import java.security.Timestamp;
import java.sql.Time;
import java.util.Date;


/**
 * Created by tiago_000 on 28/03/2015.
 */
public class Message {
    private String channel;
    private String content;
    private Date date;
    private float latitude;
    private float longitude;

    public Message (){
        this.channel= "";
        this.content= "";
        this.date= new Date();
        this.latitude= 0;
        this.longitude= 0;


    }

    public Message (String channel, String content, Date date, float latitude, float longitude){
        this.channel= channel;
        this.content= content;
        this.date= date;
        this.latitude= latitude;
        this.longitude= longitude;


    }


}

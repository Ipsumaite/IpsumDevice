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
    private float dist;
    private float alt;
    private float bearing;

    public Message (){
        this.channel= "";
        this.content= "";
        this.date= "";
        this.latitude= 0;
        this.longitude= 0;
        this.dist=-1;
        this.alt=0;
        this.bearing=-1;


    }

    public Message (String channel, String content, String date, float latitude, float longitude){
        this.channel= channel;
        this.content= content;
        this.date= date;
        this.latitude= latitude;
        this.longitude= longitude;

    }

    public Message (String channel, String content, String date, float latitude, float longitude, float dist, float alt, float bearing){
        this.channel= channel;
        this.content= content;
        this.date= date;
        this.latitude= latitude;
        this.longitude= longitude;
        this.dist= dist;
        this.alt= alt;
        this.bearing= bearing;


    }

    public float getLatitude(){
        return this.latitude;
    }

    public float getLongitude(){
        return this.longitude;
    }

    public float getDist(){
        return this.dist;
    }

    public float getAlt(){
        return this.alt;
    }

    public float getBearing(){
        return this.bearing;
    }

    public void setDist(float dist){
       this.dist=dist;
    }

    public void setAlt(float alt){
        this.alt= alt;
    }

    public void setBearing(float bearing){
        this.bearing= bearing;
    }



}

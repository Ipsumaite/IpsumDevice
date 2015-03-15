package com.ar.ipsum.ipsumapp.Resources;

/**
 * Created by QSR on 02-03-2015.
 */
public class Channel {
    private String name;
    private String description;
    private boolean subscribed;
    private String id;
    private boolean premium;
    private Subscription Subscription;

    public Channel(){
        this.name="";
        this.description="";
        this.subscribed=false;
        this.id="";
        this.premium=false;
        this.Subscription= new Subscription();
    }

    public Channel(String name,String description,boolean subscribed,String id,boolean premium,Subscription Subscription){
        this.name=name;
        this.description=description;
        this.subscribed=subscribed;
        this.id=id;
        this.premium=premium;
        this.Subscription=Subscription;
    }

}

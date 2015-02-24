package com.ar.ipsum.ipsumapp;

import android.location.Location;

public class Message {
	
	String Subject;
	String Sender;
	float lat;
	float lng;
	String msg;
	Location poistion;
	float dist;
	float alt;
	float Bearing;
	boolean selected;
	
	public Message(){
		this.Subject="";
		this.Sender="";
		this.lat=0;
		this.lng=0;
		this.msg="";
		this.poistion= new Location("Manual");
		this.dist=0;
		this.alt=0;
		this.Bearing=0;
		this.selected=false;
	}


}

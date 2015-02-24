package com.ar.ipsum.ipsumapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceJSONParser {
	 
    /** Receives a JSONObject and returns a list */
    public List<Message> parse(JSONArray jArray){
 
        JSONArray jMsg = jArray;
        return getMsgs(jMsg);
    }
 
    private List<Message> getMsgs(JSONArray jMsg){
        int MsgCount = jMsg.length();
        ArrayList<Message> MsgList = new ArrayList<Message>();
        Message msg = new Message();
 
        /** Taking each place, parses and adds to list object */
        for(int i=0; i<MsgCount;i++){
            try {
                /** Call getPlace with place JSON object to parse the place */
                msg = getMsg((JSONObject)jMsg.get(i));
                MsgList.add(msg);
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
 
        return MsgList;
    }
 
    /** Parsing the Place JSON object */
    private Message getMsg(JSONObject jMsg){
 
        Message message= new Message();
        HashMap<String, String> msg = new HashMap<String, String>();
        String subject = "";
        String body="";
        String timestamp="";
        String latitude="";
        String longitude="";
        String sender="";
 
        try {
            // Extracting Place name, if available
            if(!jMsg.isNull("subject")){
            	subject = jMsg.getString("subject");
            }
 
            // Extracting Place Vicinity, if available
            if(!jMsg.isNull("body")){
            	body = jMsg.getString("body");
            }
            if(!jMsg.isNull("longitude")){
            	longitude = jMsg.getString("longitude");
            }
            if(!jMsg.isNull("latitude")){
            	latitude = jMsg.getString("latitude");
            }

            message.Subject=subject;
            message.msg=body;
            message.lat=Float.valueOf(latitude);
            message.lng=Float.valueOf(longitude);
            message.poistion.setLatitude(Float.valueOf(latitude));
            message.poistion.setLatitude(Float.valueOf(longitude));
 
            /*place.put("place_name", placeName);
            place.put("vicinity", vicinity);
            place.put("lat", latitude);
            place.put("lng", longitude);*/
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
}
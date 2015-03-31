package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.Resources.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by tiago_000 on 29/03/2015.
 */
public class MessageJSONParser {
    public Message parse(JSONObject jObject){

        JSONObject jMessage = jObject;
        return getMsg(jMessage);
    }

    /** Parsing the Place JSON object */
    private Message getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String channel = "";
        String content="";
        Date date= new Date();
        float latitude=0;
        float longitude=0;
        Message message;


        try {
            if(!jMsg.isNull("chName")){
                channel = jMsg.getString("chName");
            }

            if(!jMsg.isNull("content")){
                content = jMsg.getString("content");
            }

            if(!jMsg.isNull("date")){
                date = new Date(Integer.parseInt(jMsg.getString("date")));
            }

            if(!jMsg.isNull("latitude")){
                latitude = Float.parseFloat(jMsg.getString("latitude"));
            }

            if(!jMsg.isNull("longitude")){
                longitude = Float.parseFloat(jMsg.getString("longitude"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        message= new Message(channel, content, date, latitude, longitude);
        return message;
    }
}

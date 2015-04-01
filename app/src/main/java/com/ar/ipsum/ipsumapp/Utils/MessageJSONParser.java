package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.Resources.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tiago_000 on 29/03/2015.
 */
public class MessageJSONParser {
    public List<Message> parse(JSONObject jObject){
        List<Message> list_msg= new ArrayList<Message>();
        JSONArray jArray= new JSONArray();
        JSONObject jMessage = jObject;

        try {
            if(!jObject.isNull("contents")){
                jArray = jObject.getJSONArray("contents");
                int p= jArray.length();
                for (int i=0; i<jArray.length();i++){
                    jMessage= jArray.getJSONObject(i);
                    list_msg.add(getMsg(jMessage));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list_msg;
    }

    /** Parsing the Place JSON object */
    private Message getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String channel = "";
        String content="";
        String date= "";
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
                date = jMsg.getString("date");
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

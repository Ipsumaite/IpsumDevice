package com.ar.ipsum.ipsumapp.Utils;


import com.ar.ipsum.ipsumapp.Resources.MyChannel;
import com.ar.ipsum.ipsumapp.Resources.Subscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tiago_000 on 29/03/2015.
 */
public class MyChannelJSONParser {
    public List<MyChannel> parse(JSONObject jObject){

        List<MyChannel> list_channels = new ArrayList<MyChannel>();
        int size=0;
        JSONObject jChannels = jObject;
        JSONArray jAChannels= new JSONArray();
        try {
            if(!jChannels.isNull("totalSize")){
                size = jChannels.getInt("totalSize");
            }
            if(!jChannels.isNull("channels")){
                jAChannels = jChannels.getJSONArray("channels");
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<size;i++){
            ;
            try {
                MyChannel mychannel= new MyChannel();
                mychannel=getMsg((JSONObject) jAChannels.get(i));
                list_channels.add(mychannel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return list_channels;
    }

    /** Parsing the Place JSON object */
    private MyChannel getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String name = "";
        String description="";
        boolean subscribed=false;
        String id="";
        boolean premium=false;
        Subscription subscription=new Subscription();
        JSONObject jsubs = new JSONObject();
        MyChannel mychannel= new MyChannel();
        boolean active= false;
        String accountid="";
        boolean visible= false;


        try {
            if(!jMsg.isNull("Name")){
                name = jMsg.getString("Name");
            }

            if(!jMsg.isNull("Description")){
                description = jMsg.getString("Description");
            }

            if(!jMsg.isNull("AccountId")){
                accountid = jMsg.getString("AccountId");
            }

            if(!jMsg.isNull("Visible")){
                visible = jMsg.getBoolean("Visible");
            }

            if(!jMsg.isNull("Active")){
                active = jMsg.getBoolean("Active");
            }

            if(!jMsg.isNull("Premium")){
                premium = jMsg.getBoolean("Premium");
            }

            if(!jMsg.isNull("Id")){
                id = jMsg.getString("Id");
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        mychannel= new MyChannel(active, description, accountid, name, id, premium, visible);
        return mychannel;
    }


}

package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.Resources.Channel;
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
public class ChannelJSONParser {
    public List<Channel> parse(JSONObject jObject){

        List<Channel> list_channels = new ArrayList<Channel>();
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
                Channel channel= new Channel();
                channel=getMsg((JSONObject) jAChannels.get(i));
                list_channels.add(channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return list_channels;
    }

    /** Parsing the Place JSON object */
    private Channel getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String name = "";
        String description="";
        boolean subscribed=false;
        String id="";
        boolean premium=false;
        Subscription subscription=new Subscription();
        JSONObject jsubs = new JSONObject();
        Channel channel= new Channel();


        try {
            if(!jMsg.isNull("Name")){
                name = jMsg.getString("Name");
            }

            if(!jMsg.isNull("Description")){
                description = jMsg.getString("Description");
            }

            if(!jMsg.isNull("Subscribed")){
                subscribed = jMsg.getBoolean("Subscribed");
            }

            if(!jMsg.isNull("Premium")){
                premium = jMsg.getBoolean("Premium");
            }

            if(!jMsg.isNull("Id")){
                id = jMsg.getString("Id");
            }

            if(!jMsg.isNull("Subscription")){
                jsubs = jMsg.getJSONObject("Subscription");
                subscription= getSubs(jsubs);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        channel= new Channel(name,description,subscribed,id,premium,subscription);
        return channel;
    }

    private Subscription getSubs(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String contractNumber = "";
        String id="";
        int contractTerm=0;
        String createdDate= "";
        String description="";
        String endDate= "";
        String startDate= "";
        String status="";
        Subscription subscription=new Subscription();



        try {
            if(!jMsg.isNull("Id")){
                id = jMsg.getString("Id");
            }

            if(!jMsg.isNull("ContractNumber")){
                contractNumber = jMsg.getString("ContractNumber");
            }

            if(!jMsg.isNull("ContractTerm")){
                contractTerm = jMsg.getInt("ContractTerm");
            }

            if(!jMsg.isNull("CreatedDate")){
                //rever
                createdDate = jMsg.getString("CreatedDate");
            }

            if(!jMsg.isNull("Description")){
                description = jMsg.getString("Description");
            }

            if(!jMsg.isNull("StartDate")){
                startDate = jMsg.getString("StartDate");
            }

            if(!jMsg.isNull("StartDate")){
                endDate = jMsg.getString("EndDate");
            }

            if(!jMsg.isNull("Status")){
                status = jMsg.getString("Status");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        subscription= new Subscription(id,contractTerm,contractNumber,createdDate,description,endDate, startDate,status);
        return subscription;
    }
}

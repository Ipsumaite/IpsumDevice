package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.IDAnswer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tiago_000 on 21/03/2015.
 */
public class PresenceJSONParser {

    public String parse(JSONObject jObject){

        JSONObject jID = jObject;
        return getMsg(jID);
    }

    /** Parsing the Place JSON object */
    private String getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String message="";






        try {
            // Extracting Place name, if available
            if(!jMsg.isNull("message")){
                message = jMsg.getString("message");
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}

package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.LoginReply;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by QSR on 27-02-2015.
 */
public class LoginJSONParser {

    public LoginReply parse(JSONObject jObject){

        JSONObject jLogin = jObject;
        return getMsg(jLogin);
    }

    /** Parsing the Place JSON object */
    private LoginReply getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String status = "";
        String token="";
        LoginReply loginReply;


        try {
            // Extracting Place name, if available
            if(!jMsg.isNull("status")){
                status = jMsg.getString("status");
            }

            if(!jMsg.isNull("token")){
                token = jMsg.getString("token");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginReply= new LoginReply(status, token);
        return loginReply;
    }
}



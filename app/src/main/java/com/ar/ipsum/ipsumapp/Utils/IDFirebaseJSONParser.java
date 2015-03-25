package com.ar.ipsum.ipsumapp.Utils;

import com.ar.ipsum.ipsumapp.IDAnswer;
import com.ar.ipsum.ipsumapp.LoginReply;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tiago_000 on 21/03/2015.
 */
public class IDFirebaseJSONParser {

    public IDAnswer parse(JSONObject jObject){

        JSONObject jID = jObject;
        return getMsg(jID);
    }

    /** Parsing the Place JSON object */
    private IDAnswer getMsg(JSONObject jMsg){


        HashMap<String, String> msg = new HashMap<String, String>();
        String email="";
        String id = "";
        IDAnswer idAnswer= new IDAnswer();




        try {
            // Extracting Place name, if available
            if(!jMsg.isNull("email")){
                email = jMsg.getString("email");
            }

            if(!jMsg.isNull("Id")){
                id = jMsg.getString("Id");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        idAnswer= new IDAnswer(email, id);
        return idAnswer;
    }
}

package com.ar.ipsum.ipsumapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ar.ipsum.ipsumapp.IDAnswer;
import com.ar.ipsum.ipsumapp.MainActivity;
import com.ar.ipsum.ipsumapp.Resources.Channel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by QSR on 18-03-2015.
 */
public class AsyncHttpGet_channels extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data
    private Context mContext;
    public static final String tokenKey = "tokenKey";
    public static final String state = "state";
    public static final String name = "nameKey";
    public static final String id = "idfirebaseKey";

    public AsyncHttpGet_channels(HashMap<String, String> data, Context context) {
        mData = data;
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {
        String data="";
        String token="";
        String email="";



        try {

            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.contains("header")){
                    token= mData.get(key);

                }else {
                    email= mData.get(key);
                }
            }

            HttpGet httpGet = new HttpGet(params[0]+email);
            HttpClient httpclient = new DefaultHttpClient();
            httpGet.setHeader("Authorization", "Bearer "+token);
            HttpResponse response = httpclient.execute(httpGet);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);


                return data;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected void onPostExecute(String result) {
        ChannelJSONParser channelJSONParser= new ChannelJSONParser();
        IDAnswer idAnswery= new IDAnswer();
        JSONObject jObject;
        SharedPreferences sharedpreferences;


        try{
            //jObject = new JSONObject(jsonData[0]);
            jObject = new JSONObject(result);
            List<Channel> channels= new ArrayList<Channel>();
            /** Getting the parsed data as a List construct */
            channels= channelJSONParser.parse(jObject);
            String email = idAnswery.getEmail();
            String idfirebase = idAnswery.getId();
            sharedpreferences= mContext.getSharedPreferences(MainActivity.MyPREFERENCES,
                    Context.MODE_PRIVATE);
            String user1=sharedpreferences.getString(name,"");
            if (user1.equals(email)){
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(id,idfirebase);
                editor.commit();

            }

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }
    }
}

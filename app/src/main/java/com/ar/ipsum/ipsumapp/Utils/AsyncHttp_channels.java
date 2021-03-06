package com.ar.ipsum.ipsumapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ar.ipsum.ipsumapp.Resources.MyChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
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
public class AsyncHttp_channels extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data
    private Context mContext;
    private String mMethod;
    private int mFlag;
    private String token="";
    private String email="";
    private String request="";
    public static final String tokenKey = "tokenKey";
    public static final String state = "state";
    public static final String name = "nameKey";
    public static final String id = "idfirebaseKey";
    public onChannelsChanged channelsChanged=null;

    public AsyncHttp_channels(HashMap<String, String> data, Context context, String method, int flag) {
        mData = data;
        mContext = context;
        channelsChanged= (onChannelsChanged) mContext;
        mMethod= method;
        mFlag= flag;


    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {
        String data="";
        token="";
        email="";
        String message="";



        try {

            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.contains("header")){
                    token= mData.get(key);

                }else if(key.contains("email")) {
                    email= mData.get(key);

                }else if(key.contains("message")) {
                    message= mData.get(key);

                }

            }

            if (mMethod.contains("Get")){
                request=params[0];
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
            }else if(mMethod.contains("Put")){
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                message= message.replace("\\","");
                message= message.replace("\"[", "[");
                message= message.replace("]\"", "]");
                StringEntity httpEntity= new StringEntity(message, "UTF8");
                httpEntity.setContentType("application/json");
                post.setEntity(httpEntity);
                post.setHeader("Authorization", "Bearer " + token);
                post.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(post);

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);


                    return data;
                }
            }






        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected void onPostExecute(String result) {
        ChannelJSONParser channelJSONParser= new ChannelJSONParser();
        MyChannelJSONParser mychannelJSONParser= new MyChannelJSONParser();

        JSONObject jObject;
        SharedPreferences sharedpreferences;

        if (mMethod.contains("Get")){
            try{

                //jObject = new JSONObject(jsonData[0]);
                jObject = new JSONObject(result);
                if (request.contains("subscriptions")){
                    //List<Channel> channels= new ArrayList<Channel>();
                    /** Getting the parsed data as a List construct */
                    //channels= channelJSONParser.parse(jObject);
                    //channelsChanged.onChannelChange((ArrayList<Channel>) channels);
                    channelsChanged.onChannelChange(jObject, "subscriptions");
                }else if (request.contains("mychannels")){
                    List<MyChannel> mychannels= new ArrayList<MyChannel>();
                    /** Getting the parsed data as a List construct */
                    ;
                    channelsChanged.onChannelChange(jObject, "mychannels");
                    //channelsChanged.onChannelChange((ArrayList<MyChannel>) channels);
                }


            }catch(Exception e){
                Log.d("Exception", e.toString());
                Toast.makeText(mContext,
                        "Error retrieving channels...", Toast.LENGTH_SHORT).show();

            }
        }else {

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("header_token", token);
            data.put("email", email);
            AsyncHttp_channels asyncHttp_channels = new AsyncHttp_channels(data, mContext, "Get", mFlag);
            asyncHttp_channels.execute("http://ipsumapi.herokuapp.com/api/subscriptions/");

        }

    }
}

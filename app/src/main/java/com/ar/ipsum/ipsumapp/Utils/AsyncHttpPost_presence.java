package com.ar.ipsum.ipsumapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ar.ipsum.ipsumapp.IDAnswer;
import com.ar.ipsum.ipsumapp.LoginReply;
import com.ar.ipsum.ipsumapp.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by QSR on 27-02-2015.
 */
public class AsyncHttpPost_presence extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;// post data
        private Context mContext;
        public static final String tokenKey = "tokenKey";
        public static final String state = "state";

        /**
         * constructor
         */
        public AsyncHttpPost_presence(HashMap<String, String> data, Context context) {
            mData = data;
            mContext = context;
        }

        /**
         * background
         */
        @Override
        protected String doInBackground(String... params) {
            String jsonstr="";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
            String type="";

            try {
                // set up post data
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                Iterator<String> it = mData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if (key.contains("header")){
                        post.setHeader("Authorization", "Bearer "+mData.get(key));

                    }else {
                        nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
                    }
                }

                post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                    HttpEntity entity = response.getEntity();
                    jsonstr = EntityUtils.toString(entity);
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
            }





            return jsonstr;
        }

        /**
         * on getting result
         */
        @Override
        protected void onPostExecute(String result) {
            PresenceJSONParser presenceJSONParser = new PresenceJSONParser();
            JSONObject jObject;
            SharedPreferences sharedpreferences;
            String message="";


            try{
                //jObject = new JSONObject(jsonData[0]);
                jObject = new JSONObject(result);

                /** Getting the parsed data as a List construct */
                message= presenceJSONParser.parse(jObject);

                if (message.contains("registered")){
                    sharedpreferences= mContext.getSharedPreferences(MainActivity.MyPREFERENCES,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(state,"Registered");
                    editor.commit();

                }

            }catch(Exception e){
                Log.d("Exception", e.toString());
            }

        }

}

package com.ar.ipsum.ipsumapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
public class AsyncHttpPost  extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;// post data
        private Context mContext;
        public static final String tokenKey = "tokenKey";
        public static final String state = "state";
        public static final String name = "nameKey";

        /**
         * constructor
         */
        public AsyncHttpPost(HashMap<String, String> data, Context context) {
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
                    nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));

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

            LoginJSONParser loginJsonParser = new LoginJSONParser();
            LoginReply loginReply= new LoginReply();
            JSONObject jObject;
            SharedPreferences sharedpreferences;


            try{
                //jObject = new JSONObject(jsonData[0]);
                jObject = new JSONObject(result);

                /** Getting the parsed data as a List construct */
                loginReply= loginJsonParser.parse(jObject);
                String mStatus = loginReply.getStatus();
                String mToken = loginReply.gettoken();
                if (mStatus.equals("Authenticated")){

                    sharedpreferences= mContext.getSharedPreferences(MainActivity.MyPREFERENCES,
                            Context.MODE_PRIVATE);

                    //request user's credentials
                    String user1=sharedpreferences.getString(name,"");
                    HashMap<String, String> data = new HashMap<String, String>();

                    data.put("header_token", mToken);
                    data.put("email", user1);
                    AsyncHttpGet_credentials asyncHttpGet = new AsyncHttpGet_credentials(data, mContext);
                    asyncHttpGet.execute("http://ipsumapi.herokuapp.com/api/accountID/");


                    String mMethod="Get";
                    int mFlag= 0;
                    AsyncHttp_channels asyncHttp_channels = new AsyncHttp_channels(data, mContext, mMethod, mFlag);
                    asyncHttp_channels.execute("http://ipsumapi.herokuapp.com/api/subscriptions/");
                    AsyncHttp_channels asyncHttp_channels1 = new AsyncHttp_channels(data, mContext, mMethod, mFlag);
                    asyncHttp_channels1.execute("http://ipsumapi.herokuapp.com/api/mychannels/");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(tokenKey,mToken);
                    editor.putString(state, mStatus);
                    editor.commit();
                    ((MainActivity)mContext).updateMenu(user1);
                    Toast.makeText(mContext,
                            user1 + " is logged in!", Toast.LENGTH_SHORT).show();

                }

            }catch(Exception e){
                Log.d("Exception", e.toString());
            }

        }



}

package com.ar.ipsum.ipsumapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ar.ipsum.ipsumapp.MainActivity;
import com.ar.ipsum.ipsumapp.R;
import com.ar.ipsum.ipsumapp.Resources.Channel;
import com.ar.ipsum.ipsumapp.Utils.AsyncHttpGet_channels;
import com.ar.ipsum.ipsumapp.Utils.AsyncHttp_channels;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tiago_000 on 02/04/2015.
 */
public class Channels_Adapter extends ArrayAdapter<Channel> {

    private Context context;
    private ArrayList<Channel> channels;
    private CheckBox subscribed;
    private TextView mName;
    private TextView description;
    private TextView premium;
    public static final String tokenKey = "tokenKey";
    public static final String state = "state";
    public static final String name = "nameKey";
    private SharedPreferences sharedpreferences;

    public Channels_Adapter(Context context, ArrayList<Channel> results) {
        super(context, R.layout.list_channels, results);
        this.context = context;
        this.channels = results;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Channel channel = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_channels, parent, false);

        sharedpreferences= context.getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        // Lookup view for data population
        mName = (TextView) rowView.findViewById(R.id.name);
        description = (TextView) rowView.findViewById(R.id.description);
        premium = (TextView) rowView.findViewById(R.id.premium);
        subscribed= (CheckBox) rowView.findViewById(R.id.subscription);

        //TextView Hora = (TextView) rowView.findViewById(R.id.text4);
        // Populate the data into the template view using the data object
        mName.setText(channel.getName());
        description.setText(channel.getDescription());
        if(channel.getPremiun()==true){
            premium.setText("Premium");
        }else {
            premium.setText("Free");
        }

        subscribed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(subscribed.isChecked()){
                    //Subscrever


                    //request user's credentials
                    String user1=sharedpreferences.getString(name,"");
                    String mToken=sharedpreferences.getString(tokenKey,"");
                    HashMap<String, String> data = new HashMap<String, String>();

                    data.put("header_token", mToken);
                    data.put("email", user1);
                    String mMethod= "Put";
                    int mFlag=0;
                    AsyncHttp_channels asyncHttp_channels = new AsyncHttp_channels(data, context, mMethod, mFlag);
                    asyncHttp_channels.execute("http://ipsumapi.herokuapp.com/api/subscriptions/");
                }else{
                    //Retirar a subscrição
                }

            }
        });

        // Return the completed view to render on screen
        return rowView;
    }


}

package com.ar.ipsum.ipsumapp;


import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ar.ipsum.ipsumapp.Resources.Channel;
import com.ar.ipsum.ipsumapp.Utils.onChannelsChanged;
import com.ar.ipsum.ipsumapp.view.Channels_Adapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChannelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Channel channel= new Channel();
    private ArrayList<Channel> channels= new ArrayList<Channel>();
    private Channels_Adapter adapter;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChannelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelFragment newInstance(String param1, String param2) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedpreferences=getActivity().getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString("Channels","");
        Type type = new TypeToken<ArrayList<Channel>>(){}.getType();
        this.channels= gson.fromJson(json, type);
        if (this.channels==null){
            this.channels= new ArrayList<Channel>();
        }

        adapter = new Channels_Adapter(inflater.getContext(),this.channels);

        /** Setting the list adapter for the ListFragment */
        setListAdapter(adapter);

        return inflater.inflate(R.layout.fragment_channel, container, false);
    }


    public void onChannelChange(ArrayList<Channel> channels) {
        this.channels.clear();
        this.channels.addAll(channels);
        adapter.notifyDataSetChanged();

    }


}

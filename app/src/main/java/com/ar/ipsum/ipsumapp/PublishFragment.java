package com.ar.ipsum.ipsumapp;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ar.ipsum.ipsumapp.Resources.Channel;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by QSR on 27-04-2015.
 */
public class PublishFragment extends Fragment {
    MapView mMapView;
    private GoogleMap googleMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private double latitude= 0.0;
    private double longitude= 0.0;
    private ArrayList<Channel> mychannels;
    private Channel  mychannel;
    public static final String id = "idfirebaseKey";;

    final String FIREBASEURL="https://glowing-heat-3433.firebaseio.com";

    Firebase myFirebaseRef;


    Spinner spinner_channel;

    ArrayAdapter<String> adapter1;
    String[] channel_items=null;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublishFragment newInstance(String param1, String param2) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
            MainActivity main = (MainActivity) getActivity();
            Firebase.setAndroidContext(main);

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_message, container, false);

        spinner_channel = (Spinner) view.findViewById(R.id.spinner_channel);
        final Button publish= (Button) view.findViewById(R.id.button);
        final EditText body= (EditText) view.findViewById(R.id.body);

        sharedpreferences=getActivity().getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString("MyChannels","");
        String idUser= sharedpreferences.getString(id,"");
        Type type = new TypeToken<ArrayList<Channel>>(){}.getType();
        this.mychannels= gson.fromJson(json, type);
        if (this.mychannels==null){
            this.mychannels= new ArrayList<Channel>();
        }
        if (mychannels.size()>0){
            channel_items = new String[mychannels.size()];
            for (int i=0; i< mychannels.size(); i++){
                channel_items[i]=mychannels.get(i).getName();
            }
        }else{
            channel_items = new String[1];
            channel_items[0]= "Empty";
        }

        myFirebaseRef= new Firebase(FIREBASEURL+"/channels/"+ idUser);
        adapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, channel_items);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_channel.setAdapter(adapter1);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude


        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Lat: " +latitude + " Lng: " + longitude);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message= body.getText().toString();
                body.setText("");
                String channelId="";
                String channel= spinner_channel.getSelectedItem().toString();
                for (int i=0; i<mychannels.size();i++){
                    if(channel.equals(mychannels.get(i).getName())){
                        channelId=mychannels.get(i).getId();
                    }
                }
                Firebase postRef = myFirebaseRef.child(channelId);

                Map<String, Object> post1 = new HashMap<String, Object>();
                post1.put("chName", channel);
                post1.put("content", message);
                post1.put("date", ServerValue.TIMESTAMP);
                post1.put("latitude", String.valueOf(latitude));
                post1.put("longitude", String.valueOf(longitude));
                postRef.push().setValue(post1);

                // Perform action on click
            }
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onChannelChange(ArrayList<Channel> channels) {
        this.mychannels.clear();
        this.mychannels.addAll(channels);
        channel_items = new String[channels.size()];
        for (int i=0; i< channels.size(); i++){
            channel_items[i]=channels.get(i).getName();
        }
        adapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, channel_items);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_channel.setAdapter(adapter1);
    }
}

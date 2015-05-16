package com.ar.ipsum.ipsumapp;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ar.ipsum.ipsumapp.Resources.*;
import com.ar.ipsum.ipsumapp.Resources.Message;
import com.ar.ipsum.ipsumapp.Utils.AsyncHttpGet_credentials;
import com.ar.ipsum.ipsumapp.Utils.AsyncHttpPost;
import com.ar.ipsum.ipsumapp.Utils.AsyncHttpPost_presence;
import com.ar.ipsum.ipsumapp.Utils.onChannelsChanged;
import com.ar.ipsum.ipsumapp.Utils.onGPSChanged;
import com.ar.ipsum.ipsumapp.Utils.onObjectSelected;
import com.ar.ipsum.ipsumapp.Utils.onOrientationChanged;
import com.ar.ipsum.ipsumapp.view.NavDrawerItem;
import com.ar.ipsum.ipsumapp.view.NavDrawerListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends Activity implements onChannelsChanged, onOrientationChanged,LocationListener, onObjectSelected,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private LocationManager locationManager;
    private SensorManager sensors;
    public SensorView sense;
    private double latitude=0;
    private double longitude=0;
    //private Camera camera;
    private RajFragment raj= new RajFragment();
    private ChannelFragment cha= new ChannelFragment();
    private MessageFragment msg= new MessageFragment();
    private PublishFragment pub= new PublishFragment();
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    public static final String tokenKey = "tokenKey";
    public static final String state = "state";
    public static final String id = "idfirebaseKey";
    public static final String lat = "latitude";
    public static final String lng = "longitude";

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    SharedPreferences sharedpreferences;
    CameraManager mCameraManager;
    onGPSChanged mCallback;
    onOrientationChanged mCallback1;
    private ArrayList<Channel> channels= new ArrayList<Channel>();
    private float[] Orientation= new float[3];
    private FusedLocationProviderApi fusedLocationProviderApi;
    private Menu menu;
    Location mCurrentLocation;
    String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        login();

        //Initiate Camera
        //Camera c = getCameraInstance();
        /*if (c != null){
            camera=c;
            arDisplay = new ArDisplayView(this.getApplicationContext(),this, camera);
            mLayout.addView(arDisplay);
        }*/
        Activity activity = this;

        try {
            mCallback1 = (onOrientationChanged) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        //Initiate orientation class
        sense= new SensorView(sensors, mCallback1);
        sense.start();

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        try {
            latitude= mCurrentLocation.getLatitude();
            longitude= mCurrentLocation.getLongitude();
            raj.onGPSChange(mCurrentLocation);
            sharedpreferences=getSharedPreferences(MyPREFERENCES,
                    Context.MODE_PRIVATE);
            String status=sharedpreferences.getString("state","");
            if (status.equals("Authenticated") || status.equals("Registered")){
                presence(""+mCurrentLocation.getLatitude(), ""+mCurrentLocation.getLongitude());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }


    @Override
    public void onChannelChange(ArrayList<Channel> channels) {
        this.channels=channels;
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedpreferences.edit();
        Gson gson = new Gson();
        String jsonchannels = gson.toJson(this.channels);
        editor1.putString("Channels", jsonchannels);
        editor1.commit();

        try {
            cha.onChannelChange(channels);
            msg.onChannelChange(channels);
            pub.onChannelChange(channels);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onOrientaionChange(float[] orientation) {
        this.Orientation= orientation;

        try {
            raj.onOrientaionChange(orientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onObjectSelect(Message msg) {
        try {
            raj.onObjectSelect(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Bundle args=new Bundle();
        switch (position) {
            case 0:
                fragment = new RajFragment();
                raj = (RajFragment) fragment;


                args.putDouble(lat, latitude);
                args.putDouble(lng, longitude);
                raj.setArguments(args);
                break;
            case 1:
                fragment = new PublishFragment();
                pub= (PublishFragment) fragment;
                args.putDouble(lat, latitude);
                args.putDouble(lng, longitude);
                pub.setArguments(args);
                break;
            case 2:
                fragment = new ChannelFragment();
                cha= (ChannelFragment) fragment;
                break;
            case 3:
                fragment = new LoginFragment();
                break;
            case 4:
                fragment = new MessageFragment();
                msg= (MessageFragment) fragment;
                args.putDouble(lat, latitude);
                args.putDouble(lng, longitude);
                msg.setArguments(args);
                break;

             /*case 5:
                fragment = new WhatsHotFragment();
                break;
             */
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void login(){
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        String user1=sharedpreferences.getString(name,"");
        String pass1=sharedpreferences.getString(pass,"");
        String token1= sharedpreferences.getString(tokenKey,"");
        SharedPreferences.Editor editor1 = sharedpreferences.edit();
        editor1.putString(state, "");
        editor1.putString(id, "");
        editor1.putString("Channels", "");
        editor1.commit();
        if(!isDataValid(user1, pass1)) {
            Toast.makeText(getBaseContext(),
                    "Login or password is incorrect", Toast.LENGTH_SHORT).show();
        } else {
            // do login request
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(name, user1);
            editor.putString(pass, pass1);
            editor.commit();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("email", user1);
            data.put("password", pass1);
            AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data, this);
            asyncHttpPost.execute("http://ipsumapi.herokuapp.com/login");
            Toast.makeText(getBaseContext(),
                    "Accessing servers...", Toast.LENGTH_SHORT).show();
        }
    }

    public void presence(String lat, String lng){
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        String user1=sharedpreferences.getString(name,"");
        String token1= sharedpreferences.getString(tokenKey,"");

        HashMap<String, String> data = new HashMap<String, String>();

        data.put("header_token", token1);
        //data.put("type", "presence");
        data.put("email", user1);
        data.put("latitude", lat);
        data.put("longitude", lng);
        AsyncHttpPost_presence asyncHttpPost_presence = new AsyncHttpPost_presence(data, this);
        asyncHttpPost_presence.execute("http://ipsumapi.herokuapp.com/api/presence");

    }

    public void getCredentials(){
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        String user1=sharedpreferences.getString(name,"");
        String token1= sharedpreferences.getString(tokenKey,"");

        HashMap<String, String> data = new HashMap<String, String>();

        data.put("header_token", token1);
        data.put("email", user1);
        AsyncHttpGet_credentials asyncHttpGet = new AsyncHttpGet_credentials(data, this);
        asyncHttpGet.execute("http://ipsumapi.herokuapp.com/api/accountID/");

    }

    public boolean isDataValid(String user, String pass) {

        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(user).matches();
        boolean isPasswordValid = !pass.isEmpty();
        return isEmailValid && isPasswordValid;
    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void updateMenu(String name){
        MenuItem item= menu.findItem(R.id.login_req);
        item.setTitle(name);
    }




}




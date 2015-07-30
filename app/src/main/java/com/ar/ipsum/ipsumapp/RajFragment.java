package com.ar.ipsum.ipsumapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ShapeDrawable;


import android.hardware.Camera;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ar.ipsum.ipsumapp.Resources.Message;
import com.ar.ipsum.ipsumapp.Utils.MessageJSONParser;
import com.ar.ipsum.ipsumapp.Utils.onGPSChanged;
import com.ar.ipsum.ipsumapp.Utils.onObjectSelected;
import com.ar.ipsum.ipsumapp.Utils.onOrientationChanged;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import rajawali.RajawaliFragment;

/**
 * Created by QSR on 16-02-2015.
 */


public class RajFragment extends RajawaliFragment implements View.OnTouchListener, SharedPreferences.OnSharedPreferenceChangeListener,
        onOrientationChanged, onGPSChanged, onObjectSelected {


    /** Output files will be saved as /sdcard/Pictures/cameratoo*.jpg */
    static final String CAPTURE_FILENAME_PREFIX = "cameratoo";
    /** Tag to distinguish log prints. */
    static final String TAG = "CameraToo";
    /** An additional thread for running tasks that shouldn't block the UI. */
    HandlerThread mBackgroundThread;
    /** Handler for running tasks in the background. */
    Handler mBackgroundHandler;
    /** Handler for running tasks on the UI thread. */
    Handler mForegroundHandler;
    /** View for displaying the camera preview. */
    SurfaceView mSurfaceView1;
    /** Used to retrieve the captured image when the user takes a snapshot. */
    ImageReader mCaptureBuffer;
    /** Handle to the Android camera services. */


    final String FIREBASEURL="https://glowing-heat-3433.firebaseio.com";

    Firebase myFirebaseRef;

    List<com.ar.ipsum.ipsumapp.Resources.Message> msgs= new ArrayList<Message>();
    List<com.ar.ipsum.ipsumapp.Resources.Message> msgs1= new ArrayList<Message>();

    Message msg_selected= new Message();






    private Renderer mRenderer;
    private SensorView sense;
    private SharedPreferences prefs;
    private String id="";
    private float[] Orientation=new float[3];
    private Location location;

    private Camera camera;
    private ArDisplayView arDisplay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mPreviewView = (FixedAspectSurfaceView) findViewById(R.id.preview);
        /*mPreviewView = new FixedAspectSurfaceView(this.getActivity());
        mPreviewView.getHolder().addCallback(this);*/
        //prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        Bundle args = getArguments();
        Double latitude=0.0;
        Double longitude=0.0;

        if (args != null) {
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
        }

        /*Camera c = getActivity().getCameraInstance();

        // If the camera was received, create the app
        if (c != null){
            camera=c;
            arDisplay = new ArDisplayView(getActivity().getApplicationContext(),this, camera);
            mLayout.addView(arDisplay);
        }*/

        prefs =this.getActivity().getApplicationContext().getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        mBackgroundThread = new HandlerThread("background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        mForegroundHandler = new Handler(getActivity().getMainLooper());

        // Inflate the SurfaceView, set it as the main layout, and attach a listener
        View layout = getActivity().getLayoutInflater().inflate(R.layout.surface, null);
        mSurfaceView1 = (SurfaceView) layout.findViewById(R.id.mainSurfaceView);


        //setContentView(mSurfaceView1);



        mSurfaceView.setZOrderMediaOverlay(true);
        mSurfaceView.setOnTouchListener(this);

        setGLBackgroundTransparent(true);

        MainActivity main = (MainActivity) getActivity();
        sense = main.sense;

        mRenderer = new Renderer(this.getActivity(), msgs, Orientation);
        mRenderer.setSurfaceView(mSurfaceView);

        super.setRenderer(mRenderer);



        Firebase.setAndroidContext(main);
        myFirebaseRef= new Firebase(FIREBASEURL);

        Message msg1= new Message("Test", "Content", "2015-04-30", 38.801146f,-9.178231f, 0, 1, 0);
        msgs1.add(msg1);
        Message msg2= new Message("Test1", "Content1", "2015-04-30", 38.798312f, -9.178661f, 0, 1, 0);
        msgs1.add(msg2);
        Message msg3= new Message("Test", "Content", "2015-04-30", 38.763595f, -9.242668f, 0, 1, 0);
        msgs1.add(msg3);
        Message msg4= new Message("Test1", "Content1", "2015-04-30", 38.762064f, -9.242701f, 0, 1, 0);
        msgs1.add(msg4);
        Message msg5= new Message("Test1", "Content1", "2015-04-30", 38.729872f, -9.146630f, 0, 1, 0);
        msgs1.add(msg5);
        Message msg6= new Message("Test", "Content", "2015-04-30", 51.611764f, -1.228317f, 0, 1, 0);
        msgs1.add(msg6);
        Message msg7= new Message("Test1", "Content1", "2015-04-30", 51.612044f, -1.228553f, 0, 1, 0);
        msgs1.add(msg7);
        Message msg8= new Message("Test2", "Content1", "2015-04-30", 51.612457f, -1.229100f, 0, 1, 0);
        msgs1.add(msg8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = (FrameLayout) inflater.inflate(R.layout.rajawali_fragment,
                container, false);



        int size2 =0;
        RelativeLayout.LayoutParams params;
        int size = 0;
        float scale = 0;
        size2 = (int) getResources().getDimension(R.dimen.message_board_size);


        params = new RelativeLayout.LayoutParams(size, size);

        mLayout.addView(mSurfaceView);
        mLayout.addView(mSurfaceView1);

        ((View) mSurfaceView).bringToFront();

        /*android:id="@+id/preview"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_weight="4"
        custom:aspectRatio="1.333"*/




        RelativeLayout rl = new RelativeLayout(this.getActivity());
        SeekBar sBar = new SeekBar(this.getActivity());
        SeekBar.OnSeekBarChangeListener abc = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Executed when progress is changed
                float[] new_orientation = new float[3];
                Orientation =new_orientation;
                System.out.println(progress);
            }
        };
        sBar.setMax(2*(int)Math.PI);
        sBar.setBackgroundColor(Color.WHITE);
        sBar.setOnSeekBarChangeListener(abc);
        /*FloatingActionButton button = new FloatingActionButton(this.getActivity());

        button.setId(R.id.fab_button);
        button.setBackground(getResources().getDrawable(R.drawable.fab_background));
        button.setElevation((int) getResources().getDimension(R.dimen.fab_elevation));
        button.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this.getActivity(), R.animator.fab_anim));*/
        scale = getResources().getDisplayMetrics().density;
        size = (int) getResources().getDimension(R.dimen.fab_size_small);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int gravity = Gravity.BOTTOM | Gravity.RIGHT; // default bottom right
        //params.gravity=gravity;
        params.bottomMargin=21;
        params.rightMargin=21;
        //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addView(sBar, params);
        /*ImageView image= new ImageView(this.getActivity());
        image.setImageResource(R.drawable.fab_icons);
        image.setDuplicateParentStateEnabled(true);
        button.addView(image);
        rl.addView(button,params);
        CardView cardView = new CardView(this.getActivity());
        cardView.setBackgroundColor(Color.WHITE);
        cardView.setElevation(2);
        cardView.setId(R.id.message_board);

        params = new RelativeLayout.LayoutParams(size2, size);
        gravity = Gravity.BOTTOM | Gravity.LEFT; // default bottom right
        //params.gravity=gravity;
        params.bottomMargin=21;
        params.leftMargin=21;
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        TextView text= new TextView(this.getActivity());
        text.setText("");
        text.setId(R.id.message);
        cardView.addView(text);
        rl.addView(cardView,params);*/



        mLayout.addView(rl);


        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mLayout != null) {
            mLayout.removeView(mSurfaceView);
            mLayout.removeView(mSurfaceView1);
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
        }
        mRenderer.onSurfaceDestroyed();
    }



    @Override
    public void onResume() {
        super.onResume();


        //PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        //PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        prefs.registerOnSharedPreferenceChangeListener(this);


        // Start a background thread to manage camera requests
        /*mBackgroundThread = new HandlerThread("background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        mForegroundHandler = new Handler(getActivity().getMainLooper());

        // Inflate the SurfaceView, set it as the main layout, and attach a listener
       /* View layout = getActivity().getLayoutInflater().inflate(R.layout.surface, null);
        mSurfaceView1 = (SurfaceView) layout.findViewById(R.id.mainSurfaceView);
        mSurfaceView1.getHolder().addCallback(mSurfaceHolderCallback);
        //setContentView(mSurfaceView1);
        mLayout.addView(mSurfaceView1);*/
        id= prefs.getString("idfirebaseKey","");
        if (!id.equals("")){
            myFirebaseRef.child("streams/"+id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                    JSONObject jObject;
                    JSONArray jArray;
                    com.ar.ipsum.ipsumapp.Resources.Message message= new com.ar.ipsum.ipsumapp.Resources.Message();
                    MessageJSONParser messageJSONParser= new MessageJSONParser();

                    Map<String, Object> map= new HashMap<String, Object>();
                    map = (Map<String, Object>) snapshot.getValue();
                    if(map!=null) {
                        int i = map.size();
                        jObject = new JSONObject(map);
                        if (location != null) {

                            //msgs = completePosition(messageJSONParser.parse(jObject), location);
                            msgs1 = completePosition(msgs1, location);
                            mRenderer.onMessagesChange(msgs1);
                        }
                    }



                }
                @Override public void onCancelled(FirebaseError error) { }


            });
        }


    }

    @Override
    public void onPause() {
        //PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();



    }

    protected boolean isTransparentSurfaceView() {
        return false;
    }






    @Override
    public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mRenderer.getObjectAt(event.getX(),event.getY());

            }

            return true;
            //return onTouchEvent(event);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("idfirebaseKey")) {
            id= prefs.getString("idfirebaseKey","");
            //myFirebaseRef.child("channels/"+id).addValueEventListener(new ValueEventListener() {
            myFirebaseRef.child("streams/"+id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                    JSONObject jObject;
                    JSONArray jArray;
                    com.ar.ipsum.ipsumapp.Resources.Message message= new com.ar.ipsum.ipsumapp.Resources.Message();
                    MessageJSONParser messageJSONParser= new MessageJSONParser();

                    Map<String, Object> map= new HashMap<String, Object>();
                    map = (Map<String, Object>) snapshot.getValue();
                    if(map!=null) {
                        int i = map.size();
                        jObject = new JSONObject(map);
                        if (location != null) {

                           //msgs = completePosition(messageJSONParser.parse(jObject), location);
                            msgs1 = completePosition(msgs1, location);
                            mRenderer.onMessagesChange(msgs1);
                        }
                    }



                }
                @Override public void onCancelled(FirebaseError error) { }


            });

        }


    }

    @Override
    public void onOrientaionChange(float[] orientation) {
        this.Orientation= orientation;
        if(mRenderer != null){
            mRenderer.onOrientaionChange(orientation);
        }

    }

    @Override
    public void onGPSChange(Location location) {
        this.location= location;
    }

    @Override
    public void onObjectSelect(Message msg) {
        msg_selected=msg;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final PopupWindow pw;
                View layout = getActivity().getLayoutInflater().inflate(R.layout.message_layout, null);
                TextView channel= (TextView) layout.findViewById(R.id.msg_channel);
                TextView msg_boddy= (TextView) layout.findViewById(R.id.msg_body);
                channel.setText(msg_selected.getChannel());
                msg_boddy.setText(msg_selected.getContent());
                pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                pw.setOutsideTouchable(true);
                pw.setBackgroundDrawable(new ShapeDrawable());
                pw.setTouchInterceptor(new View.OnTouchListener() { // or whatever you want
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) // here I want to close the pw when clicking outside it but at all this is just an example of how it works and you can implement the onTouch() or the onKey() you want
                        {
                            pw.dismiss();
                            msg_selected=new Message();
                            return true;
                        }
                        return false;
                    }

                });
                pw.showAtLocation(mLayout, Gravity.LEFT|Gravity.BOTTOM, 0, 0);
            }
        });


    }



    public List<Message> completePosition (List<Message> msgs, Location location){
        List<Message> results= new ArrayList<Message>();
        Message message= new Message();
        for(int i=0; i<msgs.size();i++){
            message=msgs.get(i);
            Location loc= new Location("manual");
            loc.setLatitude(message.getLatitude());
            loc.setLongitude(message.getLongitude());

            message.setDist(location.distanceTo(loc));
            message.setBearing(location.bearingTo(loc));
            results.add(message);
        }

        return results;
    }

}

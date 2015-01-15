package com.example.app_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;














import org.json.JSONObject;

import com.example.app_4.view.FloatingActionButton;





import rajawali.Object3D;
import rajawali.RajawaliActivity;
import rajawali.RajawaliFragment;
import rajawali.renderer.RajawaliRenderer;
import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArtutActivity extends RajawaliActivity implements OnTouchListener {
	private ArDisplayView arDisplay;
	private MessageButton UiDisplay;
	private Camera camera;
	//private MyGLSurfaceView mGLView;
	private Renderer mRenderer;
	private LocationManager locationManager;
	private SensorManager sensors;
	private SensorView sense;

	@Override 
	public void  onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Camera c = getCameraInstance();
        
        // If the camera was received, create the app
        if (c != null){
        	camera=c;
			arDisplay = new ArDisplayView(this.getApplicationContext(),this, camera);
			mLayout.addView(arDisplay);
        }
        sense= new SensorView(locationManager, c, sensors);
        sense.start();
		mSurfaceView.setZOrderMediaOverlay(true);
		mSurfaceView.setOnTouchListener(this);

		setGLBackgroundTransparent(true);

		
		RelativeLayout rl = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams params;
	    int size = 0;
	    int size2 =0;
	    float scale = 0;
		/*TextView label = new TextView(this);
		label.setText("Test");
		label.setTextSize(20);
		label.setGravity(Gravity.CENTER);
		label.setHeight(100);
		rl.addView(label);*/
		
		FloatingActionButton button = new FloatingActionButton(this);
		button.setId(R.id.fab_button);
		button.setBackground(getResources().getDrawable(R.drawable.fab_background));
		button.setElevation((int) getResources().getDimension(R.dimen.fab_elevation));
		button.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.fab_anim));
		scale = getResources().getDisplayMetrics().density;
	    size = (int) getResources().getDimension(R.dimen.fab_size_small);
	    params = new RelativeLayout.LayoutParams(size, size);
	    int gravity = Gravity.BOTTOM | Gravity.RIGHT; // default bottom right
	    //params.gravity=gravity;
	    params.bottomMargin=21;
	    params.rightMargin=21;
	    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    ImageView image= new ImageView(this);
	    image.setImageResource(R.drawable.fab_icons);
	    image.setDuplicateParentStateEnabled(true);
	    button.addView(image);
		rl.addView(button,params);
		CardView cardView = new CardView(this);
		cardView.setBackgroundColor(Color.WHITE);
		cardView.setElevation(2);
		cardView.setId(R.id.message_board);
		size2 = (int) getResources().getDimension(R.dimen.message_board_size);
		params = new RelativeLayout.LayoutParams(size2, size);
		gravity = Gravity.BOTTOM | Gravity.LEFT; // default bottom right
	    //params.gravity=gravity;
	    params.bottomMargin=21;
	    params.leftMargin=21;
	    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    TextView text= new TextView(this);
	    text.setText("");
	    text.setId(R.id.message);
	    cardView.addView(text);
	    rl.addView(cardView,params);

		
	    
		mLayout.addView(rl);
		
		mRenderer = new Renderer(this, sense);
		mRenderer.setSurfaceView(mSurfaceView);

		super.setRenderer(mRenderer);
		





	}
	
	
	
    // This method is strait for the Android API 
    // A safe way to get an instance of the Camera object. 
    public static Camera getCameraInstance(){
        Camera c = null;
 
        try {
            c = Camera.open();// attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
 
        if (arDisplay != null){
        	arDisplay = null;
        	
        }
        sense.stop();
        
        //mGLView.onPause();
    }
 
    // We call Load in our Resume method, because 
    // the app will close if we call it in onCreate
    @Override 
    protected void onResume(){
        super.onResume();
        sense.start();
       // Load();




        //mGLView.onResume();
    }



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mRenderer.getObjectAt(event.getX(),event.getY());
		}
		
		return onTouchEvent(event);
	}
    
    
    
	private int convertToPixels(int dp, float scale){
	      return (int) (dp * scale + 0.5f) ;
	    }
    
   
	

	
	
	
	
}

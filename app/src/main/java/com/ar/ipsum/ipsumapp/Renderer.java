package com.ar.ipsum.ipsumapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ar.ipsum.ipsumapp.Utils.onMessagesChanged;
import com.ar.ipsum.ipsumapp.Utils.onObjectSelected;
import com.ar.ipsum.ipsumapp.Utils.onOrientationChanged;
import com.ar.ipsum.ipsumapp.representation.Quaternion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener, onMessagesChanged, onOrientationChanged {
	private DirectionalLight mLight;
	private Object3D mSphere;
	private Object3D mCube;
	private Context context;
	private Camera camera;
	private SensorView sensor;
    private float[] mOrientation=new float[3];
    private Quaternion mQuat= new Quaternion();
    private List<Message> messages= new ArrayList<Message>();
    private List<Object3D> obj= new ArrayList<Object3D>();
    private List<Object3D> obj_text= new ArrayList<Object3D>();
    private List<Object3D> obj_msg= new ArrayList<Object3D>();
	private ObjectColorPicker mPicker;
	//private Bitmap mTimeBitmap;
	//private Canvas mTimeCanvas;
	private Paint mTextPaint;
	private AlphaMapTexture mTimeTexture;
	private SimpleDateFormat mDateFormat;
	private int mFrameCount;
	private boolean mShouldUpdateTexture;
    List<com.ar.ipsum.ipsumapp.Resources.Message> msgs= new ArrayList<com.ar.ipsum.ipsumapp.Resources.Message>();
    onObjectSelected mCallbackMsg;
	//Sphere timeSphere;
	//Sphere parentSphere;



	public Renderer(Context context, List<com.ar.ipsum.ipsumapp.Resources.Message> msgs, float[] Orientation) {
		super(context);
		this.context =context;
		//this.sensor= sense;
		setFrameRate(60);
        this.msgs=msgs;
        this.mOrientation= Orientation;

		//ArtutActivity activity = (ArtutActivity) context;
		//CardView cardView= (CardView) activity.findViewById(R.id.message_board);
		//TextView textView= (TextView) activity.findViewById(R.id.message);
		//textView.setText("mensagem");

		// TODO Auto-generated constructor stub

        Activity activity = (Activity) context;

        try {
            mCallbackMsg = (onObjectSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}

	protected void initScene() {
		//mOrientation=sensor.getRotationMatrix();
		//mQuat=sensor.getQuaternion();
		//messages=sensor.getMessages();
		mPicker = new ObjectColorPicker(this);
		mPicker.setOnObjectPickedListener(this);
		mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2);

		Material material = new Material();
        try {
			material.addTexture(new Texture("earthColors", R.drawable.earthtruecolor_nasa_big));
			material.setColorInfluence(0);
		} catch (TextureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSphere = new Sphere(1, 18, 18);
		mSphere.setMaterial(material);
		getCurrentScene().addLight(mLight);
		obj.add(mSphere);
		getCurrentScene().addChild(obj.get(0));
		mPicker.registerObject(obj.get(0));
		getCurrentCamera().setZ(0.0f);
		getCurrentCamera().setX(0.0f);
		getCurrentCamera().setY(1.0f);




	}


	public void textit(Message msg, Bitmap mTimeBitmap){
		Canvas mTimeCanvas;


		mTimeCanvas = new Canvas(mTimeBitmap);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(35);


		//
		// -- Clear the canvas, transparent
		//
		mTimeCanvas.drawColor(0, Mode.CLEAR);
		//
		// -- Draw the time on the canvas
		//
		/*mTimeCanvas.drawText(msg.Sender, 75,
				128, mTextPaint);*/
		mTimeCanvas.drawText("Titulo", 75,
				128, mTextPaint);

	}

	public void onDrawFrame(GL10 glUnused) {

        List<com.ar.ipsum.ipsumapp.Resources.Message> mMessages=msgs;
		super.onDrawFrame(glUnused);
		//mOrientation=sensor.getRotationMatrix();
		//mQuat=sensor.getQuaternion();

		if (obj.size()<mMessages.size()){
			iniciar(mMessages.size()-obj.size());
			//iniciar_text(mMessages.size()-obj_text.size());
		}
		for(int i=0; i<mMessages.size();i++){
			int n=i;
			com.ar.ipsum.ipsumapp.Resources.Message msg= mMessages.get(i);
			
			float bear_x=msg.getDist()/5*(float) (Math.sin(Math.toRadians(msg.getBearing())));
			float bear_z=msg.getDist()/5*(float) (-1*Math.cos(Math.toRadians(msg.getBearing())));
			float bear_y=msg.getDist()*0.02f;


			obj.get(i).setPosition(bear_x, bear_y, bear_z);
			obj.get(i).setRotY(obj.get(i).getRotY() + 1);
			
			//obj_text.get(i).setPosition(bear_x, 1.5, bear_z);
			
			
		}
		
        /*float x= (float)Math.sin(mOrientation[0]);
        float z= -1*(float)Math.cos(mOrientation[0]);
        float y= -1*(float)Math.sin(mOrientation[1]);*/


		float x= (float)Math.sin(0.3236751);
		float z= -1*(float)Math.cos(0.3236751);
		float y= -1*(float)Math.sin(-0.14019051);
        
		getCurrentCamera().setLookAt(x, 1+y, z);
		/*if (messages.size()>0){
			obj_msg.get(0).setPosition(x, y-2, z/Math.abs(z));
		}*/
		
	}
	
	public void iniciar (int num){
		for(int i=0; i<num;i++){
			Object3D sphere;
			Material material = new Material();
	        try {
				material.addTexture(new Texture("earthColors", R.drawable.earthtruecolor_nasa_big));
				material.setColorInfluence(0);
			} catch (TextureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        sphere = new Sphere(1, 18, 18);
	        sphere.setMaterial(material);
			getCurrentScene().addLight(mLight);
			getCurrentScene().addChild(sphere);
			mPicker.registerObject(sphere);
			obj.add(sphere);
		}
	}
	
	public void iniciar_text (int num){
		for(int i=0; i<num;i++){
			//......................................
			Bitmap mTimeBitmap;
			Canvas mTimeCanvas;
			Material timeSphereMaterial = new Material();
			//timeSphereMaterial.enableLighting(true);
			//timeSphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			mTimeBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);

			mTimeCanvas = new Canvas(mTimeBitmap);
			mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setColor(Color.WHITE);
			mTextPaint.setTextSize(35);

			//
			// -- Clear the canvas, transparent
			//
			mTimeCanvas.drawColor(Color.BLUE, Mode.CLEAR);
			//
			// -- Draw the time on the canvas
			//
			/*mTimeCanvas.drawText(msg.Sender, 75,
					128, mTextPaint);*/
			mTimeCanvas.drawText("Titulo", 75,
					128, mTextPaint);

			mTimeTexture = new AlphaMapTexture("timeTexture", mTimeBitmap);
			try {
				timeSphereMaterial.addTexture(mTimeTexture);
				timeSphereMaterial.setColorInfluence(1);
				timeSphereMaterial.setColor(Color.BLACK);
				
			} catch (TextureException e) {
				e.printStackTrace();
			}
			//Object3D plane = new Plane(1f, 2f, 1, 1);
			//Object3D timeSphere = new Sphere(1f, 12, 12);
			//plane.setMaterial(timeSphereMaterial);
			//plane.setDoubleSided(true);
			//plane.setColor((int)(0xff0000ff));
			//plane.setRotY(180);
			
			//timeSphere.setPosition(0, 0, -3);
			//timeSphere.setRenderChildrenAsBatch(true);
			//getCurrentScene().addChild(plane);
			//obj_text.add(plane);

					//........................................
			
			Material material = new Material();

			material.setColor(Color.WHITE);
			material.setColorInfluence(1f);
		
			//Object3D plane1 = new Plane(6f, 2f, 1, 1);
			//Object3D timeSphere = new Sphere(1f, 12, 12);
			//plane1.setMaterial(material);
			//plane1.setDoubleSided(true);
			//plane1.setRotZ(90);
			//plane1.setColor((int)(0xff0000ff));
			//getCurrentScene().addChild(plane1);
			//obj_msg.add(plane1);
			
		}


	}
	
	public void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	@Override
	public void onObjectPicked(Object3D object) {
		// TODO Auto-generated method stub
		//object.setZ(object.getZ() == 0 ? -2 : 0);
		for (int i=0;i<obj.size();i++){
			if(object.equals(obj.get(i))){
				Log.d("Message",msgs.get(i).getContent());
                mCallbackMsg.onObjectSelect(msgs.get(i));
			}
		}
		
		
	}

    @Override
    public void onMessagesChange(List<com.ar.ipsum.ipsumapp.Resources.Message> msgs) {
        synchronized(this){
            if (msgs.size()>0){
                this.msgs.clear();
                for(int i=0; i<msgs.size();i++){
                    this.msgs.add(msgs.get(i));
                }
            }
        }

    }

    @Override
    public void onOrientaionChange(float[] orientation) {
        synchronized (this){
            this.mOrientation= orientation;
        }

    }


}

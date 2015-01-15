package com.example.app_4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

import com.example.app_4.representation.Quaternion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.widget.TextView;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3.Axis;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener {
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
	//Sphere timeSphere;
	//Sphere parentSphere;

	

	public Renderer(Context context, SensorView sense) {
		super(context);
		this.context =context;
		this.sensor= sense;
		setFrameRate(60);
		ArtutActivity activity = (ArtutActivity) context;
		CardView cardView= (CardView) activity.findViewById(R.id.message_board);
		TextView textView= (TextView) activity.findViewById(R.id.message);
		textView.setText("mensagem");
		
		// TODO Auto-generated constructor stub
	}
	
	protected void initScene() {
		mOrientation=sensor.getRotationMatrix();
		mQuat=sensor.getQuaternion();
		messages=sensor.getMessages();
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
		getCurrentCamera().setY(0.0f);
		


	
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


		super.onDrawFrame(glUnused);
		mOrientation=sensor.getRotationMatrix();
		mQuat=sensor.getQuaternion();
		messages=sensor.getMessages();
		if (obj.size()<messages.size()){
			iniciar(messages.size()-obj.size());
			iniciar_text(messages.size()-obj_text.size());
		}
		for(int i=0; i<messages.size();i++){
			int n=i;
			Message msg= messages.get(i);
			
			float bear_x=msg.dist/10*(float) (Math.sin(Math.toRadians(msg.Bearing)));
			float bear_z=msg.dist/10*(float) (-1*Math.cos(Math.toRadians(msg.Bearing)));
			float bear_y=msg.alt/10;


			obj.get(i).setPosition(bear_x, 0, bear_z);
			obj.get(i).setRotY(obj.get(i).getRotY() + 1);
			
			//obj_text.get(i).setPosition(bear_x, 1.5, bear_z);
			
			
		}
		
        float x= 3*(float)Math.sin(mOrientation[0]);
        float z= -1*3*(float)Math.cos(mOrientation[0]);
        float y= -3*(float)Math.sin(mOrientation[1]);
        
		getCurrentCamera().setLookAt(x, y, z);
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
		
		if (obj_msg.get(0).getZ()==object.getZ()){
			obj_msg.get(0).setX(object.getX()+2);
			obj_msg.get(0).setY(object.getY()-1);
			obj_msg.get(0).setZ(object.getZ());
			//
		}
		
		
	}
}

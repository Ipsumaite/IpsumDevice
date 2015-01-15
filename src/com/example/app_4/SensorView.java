package com.example.app_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.app_4.representation.Quaternion;

public class SensorView implements SensorEventListener, LocationListener {
	
	public static final String DEBUG_TAG = "OverlayView Log";
    String accelData = "Accelerometer Data";
    String compassData = "Compass Data";
    String gyroData = "Gyro Data";
    private Location lastLocation = null;
	private LocationManager locationManager;
	private float[] lastAccelerometer= new float[3];
	private float[] lastCompass= new float[3];
	private float[] orientation=new float[3];
	private List<Message> messages= new ArrayList<Message>();
	float verticalFOV;
	float horizontalFOV;
	Camera mCamera;
	boolean init=false;
	
	private static final float NS2S = 1.0f / 1000000000.0f;
	
	private final Quaternion deltaQuaternion = new Quaternion();

    /**
     * The Quaternions that contain the current rotation (Angle and axis in Quaternion format) of the Gyroscope
     */
    private Quaternion quaternionGyroscope = new Quaternion();

    /**
     * The quaternion that contains the absolute orientation as obtained by the rotationVector sensor.
     */
    private Quaternion quaternionRotationVector = new Quaternion();

    /**
     * The time-stamp being used to record the time when the last gyroscope event occurred.
     */
    private long timestamp;
    
    private static final double EPSILON = 0.1f;

    /**
     * Value giving the total velocity of the gyroscope (will be high, when the device is moving fast and low when
     * the device is standing still). This is usually a value between 0 and 10 for normal motion. Heavy shaking can
     * increase it to about 25. Keep in mind, that these values are time-depended, so changing the sampling rate of
     * the sensor will affect this value!
     */
    private double gyroscopeRotationVelocity = 0;

    /**
     * Flag indicating, whether the orientations were initialised from the rotation vector or not. If false, the
     * gyroscope can not be used (since it's only meaningful to calculate differences from an initial state). If
     * true,
     * the gyroscope can be used normally.
     */
    private boolean positionInitialised = false;

    /**
     * Counter that sums the number of consecutive frames, where the rotationVector and the gyroscope were
     * significantly different (and the dot-product was smaller than 0.7). This event can either happen when the
     * angles of the rotation vector explode (e.g. during fast tilting) or when the device was shaken heavily and
     * the gyroscope is now completely off.
     */
    private int panicCounter;
    private int driftCounter;

    /**
     * This weight determines indirectly how much the rotation sensor will be used to correct. This weight will be
     * multiplied by the velocity to obtain the actual weight. (in sensor-fusion-scenario 2 -
     * SensorSelection.GyroscopeAndRotationVector2).
     * Must be a value between 0 and approx. 0.04 (because, if multiplied with a velocity of up to 25, should be still
     * less than 1, otherwise the SLERP will not correctly interpolate). Should be close to zero.
     */
    private static final float INDIRECT_INTERPOLATION_WEIGHT = 0.01f;

    /**
     * The threshold that indicates an outlier of the rotation vector. If the dot-product between the two vectors
     * (gyroscope orientation and rotationVector orientation) falls below this threshold (ideally it should be 1,
     * if they are exactly the same) the system falls back to the gyroscope values only and just ignores the
     * rotation vector.
     * 
     * This value should be quite high (> 0.7) to filter even the slightest discrepancies that causes jumps when
     * tiling the device. Possible values are between 0 and 1, where a value close to 1 means that even a very small
     * difference between the two sensors will be treated as outlier, whereas a value close to zero means that the
     * almost any discrepancy between the two sensors is tolerated.
     */
    private static final float OUTLIER_THRESHOLD = 0.85f;

    /**
     * The threshold that indicates a massive discrepancy between the rotation vector and the gyroscope orientation.
     * If the dot-product between the two vectors
     * (gyroscope orientation and rotationVector orientation) falls below this threshold (ideally it should be 1, if
     * they are exactly the same), the system will start increasing the panic counter (that probably indicates a
     * gyroscope failure).
     * 
     * This value should be lower than OUTLIER_THRESHOLD (0.5 - 0.7) to only start increasing the panic counter,
     * when there is a huge discrepancy between the two fused sensors.
     */
    private static final float OUTLIER_PANIC_THRESHOLD = 0.75f;

    /**
     * The threshold that indicates that a chaos state has been established rather than just a temporary peak in the
     * rotation vector (caused by exploding angled during fast tilting).
     * 
     * If the chaosCounter is bigger than this threshold, the current position will be reset to whatever the
     * rotation vector indicates.
     */
    //private static final int PANIC_THRESHOLD = 60;
    private static final int PANIC_THRESHOLD = 2;
    private static final int DRIFT_THRESHOLD = 10;
    
    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and fusion algorithm
     */
    protected final Object syncToken = new Object();

    /**
     * The list of sensors used by this provider
     */
    protected List<Sensor> sensorList = new ArrayList<Sensor>();

    /**
     * The matrix that holds the current rotation
     */
    protected final com.example.app_4.representation.Matrixf4x4 currentOrientationRotationMatrix = new com.example.app_4.representation.Matrixf4x4();
    protected Quaternion currentQuaternion = new Quaternion();
    /**
     * The quaternion that holds the current rotation
     */
    protected final com.example.app_4.representation.Quaternion currentOrientationQuaternion = new com.example.app_4.representation.Quaternion();

    /**
     * The sensor manager for accessing android sensors
     */
    protected SensorManager sensorManager;
	
	
	//boolean gotRotation =false;
	
    // angular speeds from gyro
    private float[] gyro = new float[3];
 
    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];
 
    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];
 
    // magnetic field vector
    private float[] magnet = new float[3];
 
    // accelerometer vector
    private float[] accel = new float[3];
 
    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];
 
    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];
 
    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];
    
    boolean isAccelAvailable=false;
    boolean isCompassAvailable=false;
    boolean isGyroAvailable=false;
    
	public static final int TIME_CONSTANT = 30;
	//public static final float FILTER_COEFFICIENT = 0.98f;
	public static final float FILTER_COEFFICIENT = 0.9f;
	private Timer fuseTimer = new Timer();
	private Location[] local = new Location[3];



	private final static Location mountWashington = new Location("manual");
	static {
	    mountWashington.setLatitude(38.802405);
	    mountWashington.setLongitude(-9.178654);
	    mountWashington.setAltitude(100.5d);
	}
	
	private final static Location odivelas = new Location("manual");
	static {
		odivelas.setLatitude(38.801468d);
		odivelas.setLongitude(-9.177967d);
		odivelas.setAltitude(400.5d);
	}
	
	private final static Location outro = new Location("manual");
	static {
		outro.setLatitude(38.796230d);
		outro.setLongitude(-9.178031d);
		outro.setAltitude(300.5d);
	}
	
    private float curBearingToMW;
    private float[] curBearingToMW_1=new float[3];
    private float[] dist=new float[3];
    private float[] alt=new float[3];
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    
    //private SensorManager sensorManager;
    Sensor gyroSensor;
    Sensor rotSensor;
    
    
    public SensorView (LocationManager locationManager, Camera camera, SensorManager sensorManager) {
        this.sensorManager= sensorManager;
        this.locationManager= locationManager;
        
        local[0]=mountWashington;
        local[1]=odivelas;
        local[2]=outro;
        mCamera=camera;
       
    }
    
    public void start() {
    	
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
          
        String best = locationManager.getBestProvider(criteria, true);
          
        Log.v(DEBUG_TAG,"Best provider: " + best);
          
        locationManager.requestLocationUpdates(best, 50, 0, this);
        
       
	    Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	    Sensor rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	
	    isGyroAvailable = sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    isGyroAvailable = sensorManager.registerListener(this, rotSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, TIME_CONSTANT);
    }

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
    public void stop() {
        // make sure to turn our sensors off when the activity is paused
        sensorManager.unregisterListener(this, gyroSensor);
        sensorManager.unregisterListener(this, rotSensor);

    }


    @Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
		
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
		 StringBuilder msg = new StringBuilder(event.sensor.getName()).append(" ");
		   for(float value: event.values)
		   {
		      msg.append("[").append(value).append("]");
		   }
		          
		   switch(event.sensor.getType())
		   {
		      /*case Sensor.TYPE_ACCELEROMETER:
		    	 //lastAccelerometer=event.values.clone();
		         //accelData = msg.toString();
		         System.arraycopy(event.values, 0, accel, 0, 3);
		         //calculateAccMagOrientation();
		         break;*/
		      case Sensor.TYPE_GYROSCOPE:
		            // Process Gyroscope and perform fusion
		    	    if (init==false){
		    	    	quaternionGyroscope.copyVec4(quaternionRotationVector);
		    	    	init=true;
		    	    }

		            // This timestep's delta rotation to be multiplied by the current rotation
		            // after computing it from the gyro sample data.
		            if (timestamp != 0 && positionInitialised) {
		                final float dT = (event.timestamp - timestamp) * NS2S;
		                // Axis of the rotation sample, not normalized yet.
		                float axisX = event.values[0];
		                float axisY = event.values[1];
		                float axisZ = event.values[2];

		                // Calculate the angular speed of the sample
		                gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

		                // Normalize the rotation vector if it's big enough to get the axis
		                if (gyroscopeRotationVelocity > EPSILON) {
		                    axisX /= gyroscopeRotationVelocity;
		                    axisY /= gyroscopeRotationVelocity;
		                    axisZ /= gyroscopeRotationVelocity;
		                }

		                // Integrate around this axis with the angular speed by the timestep
		                // in order to get a delta rotation from this sample over the timestep
		                // We will convert this axis-angle representation of the delta rotation
		                // into a quaternion before turning it into the rotation matrix.
		                double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
		                double sinThetaOverTwo = Math.sin(thetaOverTwo);
		                double cosThetaOverTwo = Math.cos(thetaOverTwo);
		                deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
		                deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
		                deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
		                deltaQuaternion.setW(-(float) cosThetaOverTwo);

		                // Move current gyro orientation
		                deltaQuaternion.multiplyByQuat(quaternionGyroscope, quaternionGyroscope);

		                // Calculate dot-product to calculate whether the two orientation sensors have diverged 
		                // (if the dot-product is closer to 0 than to 1), because it should be close to 1 if both are the same.
		                float dotProd = quaternionGyroscope.dotProduct(quaternionRotationVector);

		                // If they have diverged, rely on gyroscope only (this happens on some devices when the rotation vector "jumps").
		                if (Math.abs(dotProd) < OUTLIER_THRESHOLD) {
		                    // Increase panic counter
		                    if (Math.abs(dotProd) < OUTLIER_PANIC_THRESHOLD) {
		                        panicCounter++;
		                    }
		                    if (gyroscopeRotationVelocity>3){
		                    	setOrientationQuaternionAndMatrix(quaternionGyroscope);
		                    }
		                    else {

		                    // Directly use Gyro
		                    //setOrientationQuaternionAndMatrix(quaternionGyroscope);
		                    // Both are nearly saying the same. Perform normal fusion.

		                    // Interpolate with a fixed weight between the two absolute quaternions obtained from gyro and rotation vector sensors
		                    // The weight should be quite low, so the rotation vector corrects the gyro only slowly, and the output keeps responsive.
		                    Quaternion interpolate = new Quaternion();
		                    quaternionGyroscope.slerp(quaternionRotationVector, interpolate,
		                            (float) (INDIRECT_INTERPOLATION_WEIGHT * 5));

		                    // Use the interpolated value between gyro and rotationVector
		                    setOrientationQuaternionAndMatrix(interpolate);
		                    // Override current gyroscope-orientation
		                    quaternionGyroscope.copyVec4(interpolate);
		                    }
		                }else if (Math.abs(dotProd) < 0.95) {
		                    if (gyroscopeRotationVelocity>4){
		                    	setOrientationQuaternionAndMatrix(quaternionGyroscope);
		                    }
		                    else {

		                    // Directly use Gyro
		                    //setOrientationQuaternionAndMatrix(quaternionGyroscope);
		                    // Both are nearly saying the same. Perform normal fusion.

		                    // Interpolate with a fixed weight between the two absolute quaternions obtained from gyro and rotation vector sensors
		                    // The weight should be quite low, so the rotation vector corrects the gyro only slowly, and the output keeps responsive.
		                    Quaternion interpolate = new Quaternion();
		                    quaternionGyroscope.slerp(quaternionRotationVector, interpolate,
		                            (float) (INDIRECT_INTERPOLATION_WEIGHT * 3));

		                    // Use the interpolated value between gyro and rotationVector
		                    setOrientationQuaternionAndMatrix(interpolate);
		                    // Override current gyroscope-orientation
		                    quaternionGyroscope.copyVec4(interpolate);
		                    }

		                } else {
		                    // Both are nearly saying the same. Perform normal fusion.
		                	setOrientationQuaternionAndMatrix(quaternionGyroscope);
		                    // Interpolate with a fixed weight between the two absolute quaternions obtained from gyro and rotation vector sensors
		                    // The weight should be quite low, so the rotation vector corrects the gyro only slowly, and the output keeps responsive.
		                    /*Quaternion interpolate = new Quaternion();
		                    quaternionGyroscope.slerp(quaternionRotationVector, interpolate,
		                            (float) (INDIRECT_INTERPOLATION_WEIGHT * gyroscopeRotationVelocity));

		                    // Use the interpolated value between gyro and rotationVector
		                    setOrientationQuaternionAndMatrix(interpolate);
		                    // Override current gyroscope-orientation
		                    quaternionGyroscope.copyVec4(interpolate);*/

		                    // Reset the panic counter because both sensors are saying the same again
		                    panicCounter = 0;
		                }

		                if (panicCounter > PANIC_THRESHOLD) {
		                    Log.d("Rotation Vector",
		                            "Panic counter is bigger than threshold; this indicates a Gyroscope failure. Panic reset is imminent.");

		                    if (gyroscopeRotationVelocity < 3) {
		                        Log.d("Rotation Vector",
		                                "Performing Panic-reset. Resetting orientation to rotation-vector value.");

		                        // Manually set position to whatever rotation vector says.
		                        //setOrientationQuaternionAndMatrix(quaternionRotationVector);
			                    Quaternion interpolate = new Quaternion();
			                    quaternionGyroscope.slerp(quaternionRotationVector, interpolate,
			                            (float) (INDIRECT_INTERPOLATION_WEIGHT * (gyroscopeRotationVelocity+1)));

			                    // Use the interpolated value between gyro and rotationVector
			                    setOrientationQuaternionAndMatrix(interpolate);
			                    // Override current gyroscope-orientation
			                    quaternionGyroscope.copyVec4(interpolate);

		                        // Override current gyroscope-orientation with corrected value
		                        //quaternionGyroscope.copyVec4(quaternionRotationVector);

		                        panicCounter = 0;
		                    } else {
			                    Quaternion interpolate = new Quaternion();
			                    quaternionGyroscope.slerp(quaternionRotationVector, interpolate,
			                            (float) (INDIRECT_INTERPOLATION_WEIGHT * gyroscopeRotationVelocity));

			                    // Use the interpolated value between gyro and rotationVector
			                    setOrientationQuaternionAndMatrix(interpolate);
			                    // Override current gyroscope-orientation
			                    quaternionGyroscope.copyVec4(interpolate);
		                    	
		                        Log.d("Rotation Vector",
		                                String.format(
		                                        "Panic reset delayed due to ongoing motion (user is still shaking the device). Gyroscope Velocity: %.2f > 3",
		                                        gyroscopeRotationVelocity));
		                    }
		                }
		            }
		         break;
		     /* case Sensor.TYPE_MAGNETIC_FIELD:
		    	 //lastCompass=event.values.clone();
		         //compassData = msg.toString();
		         System.arraycopy(event.values, 0, magnet, 0, 3);
		         break;   */  
		      case Sensor.TYPE_ROTATION_VECTOR:
		    	  float[] q = new float[4];
		            // Calculate angle. Starting with API_18, Android will provide this value as event.values[3], but if not, we have to calculate it manually.
		            SensorManager.getQuaternionFromVector(q, event.values);

		            // Store in quaternion
		            quaternionRotationVector.setXYZW(q[1], q[2], q[3], -q[0]);
		            if (!positionInitialised) {
		                // Override
		                quaternionGyroscope.set(quaternionRotationVector);
		                positionInitialised = true;
		            }
		         break; 
		   }

             timestamp = event.timestamp;
             
		
	}
	
	/**
     * Sets the output quaternion and matrix with the provided quaternion and synchronises the setting
     * 
     * @param quaternion The Quaternion to set (the result of the sensor fusion)
     */
    private void setOrientationQuaternionAndMatrix(Quaternion quaternion) {
        Quaternion correctedQuat = quaternion.clone();
        currentQuaternion=correctedQuat;
        // We inverted w in the deltaQuaternion, because currentOrientationQuaternion required it.
        // Before converting it back to matrix representation, we need to revert this process
        correctedQuat.w(-correctedQuat.w());

       // synchronized (syncToken) {
            // Use gyro only
            currentOrientationQuaternion.copyVec4(quaternion);

            // Set the rotation matrix as well to have both representations
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, correctedQuat.ToArray());

      //  }
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastLocation = location;

        
        //lastLocation.setAltitude(200d);
		StringBuilder sb = new StringBuilder("http://megatron.derruba2000.com:8027/rcvmsg/ap0PRGOVKo7uLLnnBWw/2/2014-01-01/2014-10-31/38.800390/-9.178053/150000");
		// Creating a new non-ui thread task to download json data
		PlacesTask placesTask = new PlacesTask();
		placesTask.execute(sb.toString());



		
    	/*try {
    		Camera.Parameters params = mCamera.getParameters(); 
    		verticalFOV = params.getVerticalViewAngle(); 
    		//mRenderer.setVFov(verticalFOV);
    		horizontalFOV = params.getHorizontalViewAngle();
    		//mRenderer.setHFov(horizontalFOV);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
		try {
			for (int i=0; i<3;i++){
				curBearingToMW_1[i] = lastLocation.bearingTo(local[i]);
				dist[i]=lastLocation.distanceTo(local[i]);
				alt[i]=(float) (local[i].getAltitude()-lastLocation.getAltitude());
			}
			 //mRenderer.setBearing_1(curBearingToMW_1);
			 //mRenderer.setDist(dist);
			 //mRenderer.setAlt(alt);
			
			//curBearingToMW = lastLocation.bearingTo(mountWashington);
			
			
			//rotation = new float[9];
			  // float identity[] = new float[9];
			   //gotRotation = SensorManager.getRotationMatrix(rotation,
			     //  identity, lastAccelerometer, lastCompass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb  = new StringBuffer();

			String line = "";
			while( ( line = br.readLine())  != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}
	
	
	/** A class, to download Google Places */
	private class PlacesTask extends AsyncTask<String, Integer, String>{

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result){
			ParserTask parserTask = new ParserTask();

			// Start parsing the Google places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}

	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<Message>>{

		JSONObject jObject;
		JSONArray jArray;

		// Invoked by execute() method of this object
		@Override
		protected List<Message> doInBackground(String... jsonData) {

			List<Message> msgs = null;
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try{
				//jObject = new JSONObject(jsonData[0]);
				jArray = new JSONArray(jsonData[0]);
				
				/** Getting the parsed data as a List construct */
				msgs = placeJsonParser.parse(jArray);

			}catch(Exception e){
				Log.d("Exception",e.toString());
			}
			return msgs;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<Message> list){

			// Clears all the existing markers
			//mGoogleMap.clear();
			String aux="";
			double init_bearing;
			int n=0;
			messages=list;
			
			try {
				for (int i=0; i<messages.size();i++){
					Message message= messages.get(i);
					message.Bearing= lastLocation.bearingTo(messages.get(i).poistion);
					message.dist=lastLocation.distanceTo(messages.get(i).poistion);
					messages.set(i, message);
					//alt[i]=(float) (local[i].getAltitude()-lastLocation.getAltitude());
				}
				
				//TESTES
				Message msg= new Message();
				msg.Bearing= lastLocation.bearingTo(mountWashington);
				msg.dist=lastLocation.distanceTo(mountWashington);
				msg.alt=200;
				msg.lat=(float) mountWashington.getLatitude();
				msg.lng=(float) mountWashington.getLongitude();
				msg.msg="edjfdcsh";
				
				messages.add(msg);
				
				msg= new Message();
				msg.Bearing= lastLocation.bearingTo(odivelas);
				msg.dist=lastLocation.distanceTo(odivelas);
				msg.alt=200;
				msg.lat=(float) odivelas.getLatitude();
				msg.lng=(float) odivelas.getLongitude();
				msg.msg="edjfdcsh";
				
				messages.add(msg);
				
				msg= new Message();
				msg.Bearing= lastLocation.bearingTo(outro);
				msg.dist=lastLocation.distanceTo(outro);
				msg.alt=200;
				msg.lat=(float) outro.getLatitude();
				msg.lng=(float) outro.getLongitude();
				msg.msg="edjfdcsh";
				
				messages.add(msg);
				
				
				
				//mRenderer.setMessages(messages);
				//curBearingToMW = lastLocation.bearingTo(mountWashington);
				
				
				//rotation = new float[9];
				  // float identity[] = new float[9];
				   //gotRotation = SensorManager.getRotationMatrix(rotation,
				     //  identity, lastAccelerometer, lastCompass);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}
	
	class calculateFusedOrientationTask extends TimerTask {
	    private float curBearingToMW;
	    private float[] curBearingToMW_1=new float[3];
	    private float[] dist=new float[3];
	    private float[] alt=new float[3];


		public void run() {
			
            com.example.app_4.representation.Matrixf4x4 RotationMatrix = new com.example.app_4.representation.Matrixf4x4();
            //RotationMatrix=currentOrientationRotationMatrix;
            //SensorManager.remapCoordinateSystem(currentOrientationRotationMatrix.matrix, SensorManager.AXIS_Z,
		    //        SensorManager.AXIS_MINUS_X, gyroOrientation);
            RotationMatrix.matrix= currentOrientationRotationMatrix.matrix.clone();
            SensorManager.remapCoordinateSystem(currentOrientationRotationMatrix.matrix, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X,RotationMatrix.matrix);
            SensorManager.getOrientation(RotationMatrix.matrix, accMagOrientation);
            
            
	    	
	    	
	    	
            
			
	    	try {
	    		Camera.Parameters params = mCamera.getParameters(); 
	    		verticalFOV = params.getVerticalViewAngle(); 
	    		//mRenderer.setVFov(verticalFOV);
	    		horizontalFOV = params.getHorizontalViewAngle();
	    		//mRenderer.setHFov(horizontalFOV);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			try {
				for (int i=0; i<3;i++){
					curBearingToMW_1[i] = lastLocation.bearingTo(local[i]);
					dist[i]=lastLocation.distanceTo(local[i]);
					alt[i]=(float) (local[i].getAltitude()-lastLocation.getAltitude());
				}
				
				//curBearingToMW = lastLocation.bearingTo(mountWashington);
				
				
				//rotation = new float[9];
				  // float identity[] = new float[9];
				   //gotRotation = SensorManager.getRotationMatrix(rotation,
				     //  identity, lastAccelerometer, lastCompass);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 //mRenderer.setQuat(currentOrientationQuaternion);
			 //mRenderer.setOrientation(accMagOrientation);
			//mRenderer.setOrientation(accMagOrientation);
			 //mRenderer.setBearing_1(curBearingToMW_1);
			 //mRenderer.setDist(dist);
			 //mRenderer.setAlt(alt);
			 //requestRender();
			 //mRenderer.setBearing(curBearingToMW);
	    }
	}
	
	
	
    public float[] getRotationMatrix() {
        synchronized (syncToken) {
            return accMagOrientation;
        }
    }
    
    public Quaternion getQuaternion() {
        synchronized (syncToken) {
            return currentQuaternion;
        }
    }
    
    public List<Message> getMessages() {
        synchronized (syncToken) {
            return messages;
        }
    }
}

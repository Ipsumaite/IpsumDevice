package com.example.app_4;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class OverlayView extends View implements SensorEventListener, LocationListener {
	  
    public static final String DEBUG_TAG = "OverlayView Log";
    String accelData = "Accelerometer Data";
    String compassData = "Compass Data";
    String gyroData = "Gyro Data";
    private Location lastLocation = null;
	private LocationManager locationManager;
	private float[] lastAccelerometer= new float[3];
	private float[] lastCompass= new float[3];
	private float[] orientation=new float[3];
	float verticalFOV;
	float horizontalFOV;
	Camera mCamera;
	boolean gotRotation =false;
	
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
	
	private final static Location mountWashington = new Location("manual");
	static {
	    mountWashington.setLatitude(38.800749d);
	    mountWashington.setLongitude(-9.179040d);
	    mountWashington.setAltitude(100.5d);
	}
	
    
	public static final int TIME_CONSTANT = 30;
	public static final float FILTER_COEFFICIENT = 0.98f;
	private Timer fuseTimer = new Timer();
      
    public OverlayView(Context context, Camera camera) {
        super(context);  
        
        
        mCamera=camera;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
          
        String best = locationManager.getBestProvider(criteria, true);
          
        Log.v(DEBUG_TAG,"Best provider: " + best);
          
        locationManager.requestLocationUpdates(best, 50, 0, this);
        
        SensorManager sensors = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    Sensor accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    Sensor compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    Sensor gyroSensor = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	     
	    isAccelAvailable = sensors.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    isCompassAvailable = sensors.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);      
	    isGyroAvailable = sensors.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        
	    gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;
 
        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
 

        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, TIME_CONSTANT);

    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
	    
    	try {
    		Camera.Parameters params = mCamera.getParameters(); 
    		verticalFOV = params.getVerticalViewAngle(); 
    		horizontalFOV = params.getHorizontalViewAngle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
        Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Align.CENTER);
        contentPaint.setTextSize(20);
        contentPaint.setColor(Color.RED);
        //canvas.drawText(accelData, canvas.getWidth()/2, canvas.getHeight()/4, contentPaint);
        //canvas.drawText(compassData, canvas.getWidth()/2, canvas.getHeight()/2, contentPaint);
        //canvas.drawText(gyroData, canvas.getWidth()/2, (canvas.getHeight()*3)/4, contentPaint);
        
        float curBearingToMW = 0;

		// compute rotation matrix
		//float rotation[] = null;
		//boolean gotRotation=false;
		try {
			curBearingToMW = lastLocation.bearingTo(mountWashington);
			
			//rotation = new float[9];
			  // float identity[] = new float[9];
			   //gotRotation = SensorManager.getRotationMatrix(rotation,
			     //  identity, lastAccelerometer, lastCompass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//isCompassAvailable=false;
		if (isAccelAvailable==true && isCompassAvailable==true){
		
			  
			   /*float cameraRotation[] = new float[9];
			    // remap such that the camera is pointing straight down the Y axis
			   SensorManager.remapCoordinateSystem(rotation, SensorManager.AXIS_Z,
			            SensorManager.AXIS_MINUS_X, cameraRotation);
			  
			    // orientation vector
			    orientation = new float[3];
			    SensorManager.getOrientation(cameraRotation, orientation);*/

			  // orientation=accMagOrientation;
			   //orientation=gyroOrientation;
			      orientation=fusedOrientation;		
			   }else {
				   orientation=gyroOrientation;
			   }
		   
		   
	
		// use roll for screen rotation
		   canvas.rotate((float)(0.0f- Math.toDegrees(orientation[2])));
		   // Translate, but normalize for the FOV of the camera -- basically, pixels per degree, times degrees == pixels
		   float dx = (float) ( (canvas.getWidth()/ horizontalFOV) * (Math.toDegrees(orientation[0])-curBearingToMW));
		   float dy = (float) ( (canvas.getHeight()/ verticalFOV) * Math.toDegrees(orientation[1])) ;
		                     
		   // wait to translate the dx so the horizon doesn't get pushed off
		   canvas.translate(0.0f, 0.0f-dy);
		     
		   // make our line big enough to draw regardless of rotation and translation              
		   canvas.drawLine(0f - canvas.getHeight(), canvas.getHeight()/2, canvas.getWidth()+canvas.getHeight(), canvas.getHeight()/2, contentPaint);
		     
		     
		   // now translate the dx
		   canvas.translate(0.0f-dx, 0.0f);
		     
		   // draw our point -- we've rotated and translated this to the right spot already
		   canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 8.0f, contentPaint);

		   
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
		      case Sensor.TYPE_ACCELEROMETER:
		    	 //lastAccelerometer=event.values.clone();
		         //accelData = msg.toString();
		         //System.arraycopy(event.values, 0, accel, 0, 3);
		         calculateAccMagOrientation();
		         break;
		      case Sensor.TYPE_GYROSCOPE:
		         //gyroData = msg.toString();
		         gyroFunction(event);
		         break;
		      case Sensor.TYPE_MAGNETIC_FIELD:
		    	 //lastCompass=event.values.clone();
		         //compassData = msg.toString();
		        // System.arraycopy(event.values, 0, magnet, 0, 3);
		         break;              
		   }
		   
		   this.invalidate();  
		   
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastLocation = location; 
		
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
	
	
	public static final float EPSILON = 0.000000001f;
	 
	private void getRotationVectorFromGyro(float[] gyroValues,
	                                       float[] deltaRotationVector,
	                                       float timeFactor)
	{
	    float[] normValues = new float[3];
	 
	    // Calculate the angular speed of the sample
	    float omegaMagnitude =
	        (float)Math.sqrt(gyroValues[0] * gyroValues[0] +
	        gyroValues[1] * gyroValues[1] +
	        gyroValues[2] * gyroValues[2]);
	 
	    // Normalize the rotation vector if it's big enough to get the axis
	    if(omegaMagnitude > EPSILON) {
	        normValues[0] = gyroValues[0] / omegaMagnitude;
	        normValues[1] = gyroValues[1] / omegaMagnitude;
	        normValues[2] = gyroValues[2] / omegaMagnitude;
	    }
	 
	    // Integrate around this axis with the angular speed by the timestep
	    // in order to get a delta rotation from this sample over the timestep
	    // We will convert this axis-angle representation of the delta rotation
	    // into a quaternion before turning it into the rotation matrix.
	    float thetaOverTwo = omegaMagnitude * timeFactor;
	    float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
	    float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
	    deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
	    deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
	    deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
	    deltaRotationVector[3] = cosThetaOverTwo;
	}
	
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private boolean initState = true;
	 
	public void gyroFunction(SensorEvent event) {
		SensorEvent event1=event;

			// don't start until first accelerometer/magnetometer orientation has been acquired
			if (accMagOrientation == null){
			    return;
		    }

		    // initialisation of the gyroscope based rotation matrix
		    if(initState) {
		        float[] initMatrix = new float[9];
		        initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
		        float[] test = new float[3];
		        SensorManager.getOrientation(initMatrix, test);
		        gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
		        initState = false;
		    }
		 
		    // copy the new gyro values into the gyro array
		    // convert the raw gyro data into a rotation vector
		    float[] deltaVector = new float[4];
		    if(timestamp != 0) {
		        final float dT = (event1.timestamp - timestamp) * NS2S;
		    System.arraycopy(event1.values, 0, gyro, 0, 3);
		    getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
		    }
		 
		    // measurement done, save current time for next interval
		    timestamp = event1.timestamp;
		 
		    // convert rotation vector into rotation matrix
		    float[] deltaMatrix = new float[9];
		    SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
		 
		    // apply the new rotation interval on the gyroscope based rotation matrix
		    gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);
		 
		    // get the gyroscope based orientation from the rotation matrix
	    	SensorManager.remapCoordinateSystem(gyroMatrix, SensorManager.AXIS_Z,
		            SensorManager.AXIS_MINUS_X, gyroOrientation);
		    SensorManager.getOrientation(gyroMatrix, gyroOrientation);

	   
	}
	
	private float[] getRotationMatrixFromOrientation(float[] o) {
	    float[] xM = new float[9];
	    float[] yM = new float[9];
	    float[] zM = new float[9];
	 
	    float sinX = (float)Math.sin(o[1]);
	    float cosX = (float)Math.cos(o[1]);
	    float sinY = (float)Math.sin(o[2]);
	    float cosY = (float)Math.cos(o[2]);
	    float sinZ = (float)Math.sin(o[0]);
	    float cosZ = (float)Math.cos(o[0]);
	 
	    // rotation about x-axis (pitch)
	    xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
	    xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
	    xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
	 
	    // rotation about y-axis (roll)
	    yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
	    yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
	    yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
	 
	    // rotation about z-axis (azimuth)
	    zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
	    zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
	    zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
	 
	    // rotation order is y, x, z (roll, pitch, azimuth)
	    float[] resultMatrix = matrixMultiplication(xM, yM);
	    resultMatrix = matrixMultiplication(zM, resultMatrix);
	    return resultMatrix;
	}
	
	private float[] matrixMultiplication(float[] A, float[] B) {
	    float[] result = new float[9];
	 
	    result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
	    result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
	    result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
	 
	    result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
	    result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
	    result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
	 
	    result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
	    result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
	    result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
	 
	    return result;
	}
	
	class calculateFusedOrientationTask extends TimerTask {
	    public void run() {
	        float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
	        fusedOrientation[0] =
	            FILTER_COEFFICIENT * gyroOrientation[0]
	            + oneMinusCoeff * accMagOrientation[0];
	 
	        fusedOrientation[1] =
	            FILTER_COEFFICIENT * gyroOrientation[1]
	            + oneMinusCoeff * accMagOrientation[1];
	 
	        fusedOrientation[2] =
	            FILTER_COEFFICIENT * gyroOrientation[2]
	            + oneMinusCoeff * accMagOrientation[2];
	 
	        // overwrite gyro matrix and orientation with fused orientation
	        // to comensate gyro drift
			if (isAccelAvailable==true && isCompassAvailable==true){	
		        gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
		        System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
			}else{
				gyroMatrix = getRotationMatrixFromOrientation(gyroOrientation);
			}
	    }
	}
	

	
	
	
	public void calculateAccMagOrientation() {
	    if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
	    	float[] accMagRotation = new float[9];
			SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Z,
		            SensorManager.AXIS_MINUS_X, accMagRotation);

	        SensorManager.getOrientation(accMagRotation, accMagOrientation);
	        if (accMagOrientation[0]==0 && accMagOrientation[1]==0 && accMagOrientation[2]==0){
	        	gotRotation=false;
	        }else{
	        	gotRotation=true;
	        }
	    }
	}
	


	public void gyro(SensorEvent event) {
	  // This timestep's delta rotation to be multiplied by the current rotation
	  // after computing it from the gyro sample data.
	  float[] deltaVector = new float[4];
	  if (timestamp != 0) {
	    final float dT = (event.timestamp - timestamp) * NS2S;
	    // Axis of the rotation sample, not normalized yet.
	    float axisX = event.values[0];
	    float axisY = event.values[1];
	    float axisZ = event.values[2];

	    // Calculate the angular speed of the sample
	    float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

	    // Normalize the rotation vector if it's big enough to get the axis
	    // (that is, EPSILON should represent your maximum allowable margin of error)
	    if (omegaMagnitude > EPSILON) {
	      axisX /= omegaMagnitude;
	      axisY /= omegaMagnitude;
	      axisZ /= omegaMagnitude;
	    }

	    // Integrate around this axis with the angular speed by the timestep
	    // in order to get a delta rotation from this sample over the timestep
	    // We will convert this axis-angle representation of the delta rotation
	    // into a quaternion before turning it into the rotation matrix.
	    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
	    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
	    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
	    
	    deltaVector[0] = sinThetaOverTwo * axisX;
	    deltaVector[1] = sinThetaOverTwo * axisY;
	    deltaVector[2] = sinThetaOverTwo * axisZ;
	    deltaVector[3] = cosThetaOverTwo;
	  }
	  timestamp = event.timestamp;
	  float[] deltaRotationMatrix = new float[9];
	  SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaVector);
	    // User code should concatenate the delta rotation we computed with the current rotation
	    // in order to get the updated rotation.
	    // rotationCurrent = rotationCurrent * deltaRotationMatrix;
	    // get the gyroscope based orientation from the rotation matrix
	    // apply the new rotation interval on the gyroscope based rotation matrix
	    gyroMatrix = matrixMultiplication(gyroMatrix, deltaRotationMatrix);
	 
	    // get the gyroscope based orientation from the rotation matrix
  	    SensorManager.remapCoordinateSystem(gyroMatrix, SensorManager.AXIS_Z,
	            SensorManager.AXIS_MINUS_X, gyroOrientation);
	    SensorManager.getOrientation(gyroMatrix, gyroOrientation);
	  

	}
}
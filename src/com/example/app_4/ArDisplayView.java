package com.example.app_4;

import java.io.IOException;
import java.util.List;



import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ArDisplayView extends SurfaceView implements SurfaceHolder.Callback 
{
    public static final String DEBUG_TAG = "ArDisplayView Log";
    Camera mCamera;
    SurfaceHolder mHolder;
    Activity mActivity;
	private Camera cam;
	private int height;
	private int width;
	Camera.Parameters params;
	Context context1;

      
    public ArDisplayView(Context context, Activity activity, Camera camera) { 
        super(context);
        
        mCamera= camera;
  
        mActivity = activity;
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        mHolder.addCallback(this); 

  
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
		   params = mCamera.getParameters();     
		   List<Size> prevSizes = params.getSupportedPreviewSizes();
		   for (Size s : prevSizes)
		   {
		      if((s.height <= height) && (s.width <= width))
		      {
		         params.setPreviewSize(s.width, s.height);
		         height=s.height;
		         width=s.width;
		         break;
		      } 
		   }
		             
		   mCamera.setParameters(params);
		   mCamera.startPreview(); 
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		//mCamera = Camera.open();
          
		   CameraInfo info = new CameraInfo();     
		   Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);          
		   int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();  
		   int degrees = 0;    
		   switch (rotation) {      
		      case Surface.ROTATION_0: degrees = 0; break;         
		      case Surface.ROTATION_90: degrees = 90; break;       
		      case Surface.ROTATION_180: degrees = 180; break;        
		      case Surface.ROTATION_270: degrees = 270; break;    
		   }     
		   mCamera.setDisplayOrientation((info.orientation - degrees + 360) % 360);
		          
		   try {
		       mCamera.setPreviewDisplay(mHolder);
		   } catch (IOException e) {
		      Log.e(DEBUG_TAG, "surfaceCreated exception: ", e);    
		   } 
		   

		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		 mCamera.stopPreview();
		 mCamera.release();   
		
	}
	
	public Camera getcamera(){
		return mCamera;
	}
	
	// Container Activity must implement this interface
	public interface OnDataChangeListener {
		public void onDataChange(Camera mCamera);
	}
	
	

}

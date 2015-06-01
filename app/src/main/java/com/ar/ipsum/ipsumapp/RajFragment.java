package com.ar.ipsum.ipsumapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
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
    CameraManager mCameraManager;
    /** The specific camera device that we're using. */
    CameraDevice mCamera;
    /** Our image capture session. */
    CameraCaptureSession mCaptureSession;

    final String FIREBASEURL="https://glowing-heat-3433.firebaseio.com";

    Firebase myFirebaseRef;

    List<com.ar.ipsum.ipsumapp.Resources.Message> msgs= new ArrayList<Message>();
    List<com.ar.ipsum.ipsumapp.Resources.Message> msgs1= new ArrayList<Message>();

    Message msg_selected= new Message();



    static Size chooseBigEnoughSize(Size[] choices, int width, int height) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            if (option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private Renderer mRenderer;
    private SensorView sense;
    private SharedPreferences prefs;
    private String id="";
    private float[] Orientation=new float[3];
    private Location location;




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

        prefs =this.getActivity().getApplicationContext().getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        mBackgroundThread = new HandlerThread("background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        mForegroundHandler = new Handler(getActivity().getMainLooper());

        // Inflate the SurfaceView, set it as the main layout, and attach a listener
        View layout = getActivity().getLayoutInflater().inflate(R.layout.surface, null);
        mSurfaceView1 = (SurfaceView) layout.findViewById(R.id.mainSurfaceView);
        mSurfaceView1.getHolder().addCallback(mSurfaceHolderCallback);

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


        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
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




        /*RelativeLayout rl = new RelativeLayout(this.getActivity());
        FloatingActionButton button = new FloatingActionButton(this.getActivity());
        button.setId(R.id.fab_button);
        button.setBackground(getResources().getDrawable(R.drawable.fab_background));
        button.setElevation((int) getResources().getDimension(R.dimen.fab_elevation));
        button.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this.getActivity(), R.animator.fab_anim));
        scale = getResources().getDisplayMetrics().density;
        size = (int) getResources().getDimension(R.dimen.fab_size_small);
        params = new RelativeLayout.LayoutParams(size, size);
        int gravity = Gravity.BOTTOM | Gravity.RIGHT; // default bottom right
        //params.gravity=gravity;
        params.bottomMargin=21;
        params.rightMargin=21;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ImageView image= new ImageView(this.getActivity());
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



        //mLayout.addView(rl);


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

        try {
            // Ensure SurfaceHolderCallback#surfaceChanged() will run again if the user returns
            mSurfaceView1.getHolder().setFixedSize(/*width*/0, /*height*/0);
            // Cancel any stale preview jobs
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
        } finally {
            if (mCamera != null) {
                mCamera.close();
                mCamera = null;
            }
        }
        // Finish processing posted messages, then join on the handling thread
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
        } catch (InterruptedException ex) {
            Log.e(TAG, "Background worker thread was interrupted while joined", ex);
        }
        // Close the ImageReader now that the background thread has stopped
        if (mCaptureBuffer != null) mCaptureBuffer.close();



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


    /**
            * Compares two {@code Size}s based on their areas.
    */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
    /**
     * Called when our {@code Activity} gains focus. <p>Starts initializing the camera.</p>
     */


    public void onClickOnSurfaceView(View v) {
        if (mCaptureSession != null) {
            try {
                CaptureRequest.Builder requester =
                        mCamera.createCaptureRequest(mCamera.TEMPLATE_STILL_CAPTURE);
                requester.addTarget(mCaptureBuffer.getSurface());
                try {
                    // This handler can be null because we aren't actually attaching any callback
                    mCaptureSession.capture(requester.build(), /*listener*/null, /*handler*/null);
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Failed to file actual capture request", ex);
                }
            } catch (CameraAccessException ex) {
                Log.e(TAG, "Failed to build actual capture request", ex);
            }
        } else {
            Log.e(TAG, "User attempted to perform a capture outside our session");
        }
        // Control flow continues in mImageCaptureListener.onImageAvailable()
    }
    /**
     * Callbacks invoked upon state changes in our {@code SurfaceView}.
     */
    final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        /** The camera device to use, or null if we haven't yet set a fixed surface size. */
        private String mCameraId;
        /** Whether we received a change callback after setting our fixed surface size. */
        private boolean mGotSecondCallback;
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // This is called every time the surface returns to the foreground
            Log.i(TAG, "Surface created");
            mCameraId = null;
            mGotSecondCallback = false;
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "Surface destroyed");
            holder.removeCallback(this);
            // We don't stop receiving callbacks forever because onResume() will reattach us
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // On the first invocation, width and height were automatically set to the view's size
            if (mCameraId == null) {
                // Find the device's back-facing camera and set the destination buffer sizes
                try {
                    for (String cameraId : mCameraManager.getCameraIdList()) {
                        CameraCharacteristics cameraCharacteristics =
                                mCameraManager.getCameraCharacteristics(cameraId);
                        if (cameraCharacteristics.get(cameraCharacteristics.LENS_FACING) ==
                                CameraCharacteristics.LENS_FACING_BACK) {
                            Log.i(TAG, "Found a back-facing camera");
                            StreamConfigurationMap info = cameraCharacteristics
                                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                            // Bigger is better when it comes to saving our image
                            Size largestSize = Collections.max(
                                    Arrays.asList(info.getOutputSizes(ImageFormat.JPEG)),
                                    new CompareSizesByArea());
                            // Prepare an ImageReader in case the user wants to capture images
                            Log.i(TAG, "Capture size: " + largestSize);
                            mCaptureBuffer = ImageReader.newInstance(largestSize.getWidth(),
                                    largestSize.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                            mCaptureBuffer.setOnImageAvailableListener(
                                    mImageCaptureListener, mBackgroundHandler);
                            // Danger, W.R.! Attempting to use too large a preview size could
                            // exceed the camera bus' bandwidth limitation, resulting in
                            // gorgeous previews but the storage of garbage capture data.
                            Log.i(TAG, "SurfaceView size: " +
                                    mSurfaceView1.getWidth() + 'x' + mSurfaceView1.getHeight());
                            Size optimalSize = chooseBigEnoughSize(
                                    info.getOutputSizes(SurfaceHolder.class), width, height);
                            // Set the SurfaceHolder to use the camera's largest supported size
                            Log.i(TAG, "Preview size: " + optimalSize);
                            SurfaceHolder surfaceHolder = mSurfaceView1.getHolder();
                            surfaceHolder.setFixedSize(optimalSize.getWidth(),
                                    optimalSize.getHeight());
                            mCameraId = cameraId;
                            return;
                            // Control flow continues with this method one more time
                            // (since we just changed our own size)
                        }
                    }
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Unable to list cameras", ex);
                }
                Log.e(TAG, "Didn't find any back-facing cameras");
                // This is the second time the method is being invoked: our size change is complete
            } else if (!mGotSecondCallback) {
                if (mCamera != null) {
                    Log.e(TAG, "Aborting camera open because it hadn't been closed");
                    return;
                }
                // Open the camera device
                try {
                    mCameraManager.openCamera(mCameraId, mCameraStateCallback,
                            mBackgroundHandler);
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Failed to configure output surface", ex);
                }
                mGotSecondCallback = true;
                // Control flow continues in mCameraStateCallback.onOpened()
            }
        }};
    /**
     * Calledbacks invoked upon state changes in our {@code CameraDevice}. <p>These are run on
     * {@code mBackgroundThread}.</p>
     */
    final CameraDevice.StateCallback mCameraStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.i(TAG, "Successfully opened camera");
                    mCamera = camera;
                    try {
                        List<Surface> outputs = Arrays.asList(
                                mSurfaceView1.getHolder().getSurface(), mCaptureBuffer.getSurface());
                        camera.createCaptureSession(outputs, mCaptureSessionListener,
                                mBackgroundHandler);
                    } catch (CameraAccessException ex) {
                        Log.e(TAG, "Failed to create a capture session", ex);
                    }
                    // Control flow continues in mCaptureSessionListener.onConfigured()
                }
                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.e(TAG, "Camera was disconnected");
                }
                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.e(TAG, "State error on device '" + camera.getId() + "': code " + error);
                }};
    /**
     * Callbacks invoked upon state changes in our {@code CameraCaptureSession}. <p>These are run on
     * {@code mBackgroundThread}.</p>
     */
    final CameraCaptureSession.StateCallback mCaptureSessionListener =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    Log.i(TAG, "Finished configuring camera outputs");
                    mCaptureSession = session;
                    SurfaceHolder holder = mSurfaceView1.getHolder();
                    if (holder != null) {
                        try {
                            // Build a request for preview footage
                            CaptureRequest.Builder requestBuilder =
                                    mCamera.createCaptureRequest(mCamera.TEMPLATE_PREVIEW);
                            requestBuilder.addTarget(holder.getSurface());
                            CaptureRequest previewRequest = requestBuilder.build();
                            // Start displaying preview images
                            try {
                                session.setRepeatingRequest(previewRequest, /*listener*/null,
                                /*handler*/null);
                            } catch (CameraAccessException ex) {
                                Log.e(TAG, "Failed to make repeating preview request", ex);
                            }
                        } catch (CameraAccessException ex) {
                            Log.e(TAG, "Failed to build preview request", ex);
                        }
                    }
                    else {
                        Log.e(TAG, "Holder didn't exist when trying to formulate preview request");
                    }
                }
                @Override
                public void onClosed(CameraCaptureSession session) {
                    mCaptureSession = null;
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "Configuration error on device '" + mCamera.getId());
                }};
    /**
     * Callback invoked when we've received a JPEG image from the camera.
     */
    final ImageReader.OnImageAvailableListener mImageCaptureListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // Save the image once we get a chance
                    mBackgroundHandler.post(new CapturedImageSaver(reader.acquireNextImage()));
                    // Control flow continues in CapturedImageSaver#run()
                }};
    /**
     * Deferred processor responsible for saving snapshots to disk. <p>This is run on
     * {@code mBackgroundThread}.</p>
     */
    static class CapturedImageSaver implements Runnable {
        /** The image to save. */
        private Image mCapture;
        public CapturedImageSaver(Image capture) {
            mCapture = capture;
        }
        @Override
        public void run() {
            try {
                // Choose an unused filename under the Pictures/ directory
                File file = File.createTempFile(CAPTURE_FILENAME_PREFIX, ".jpg",
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES));
                try (FileOutputStream ostream = new FileOutputStream(file)) {
                    Log.i(TAG, "Retrieved image is" +
                            (mCapture.getFormat() == ImageFormat.JPEG ? "" : "n't") + " a JPEG");
                    ByteBuffer buffer = mCapture.getPlanes()[0].getBuffer();
                    Log.i(TAG, "Captured image size: " +
                            mCapture.getWidth() + 'x' + mCapture.getHeight());
                    // Write the image out to the chosen file
                    byte[] jpeg = new byte[buffer.remaining()];
                    buffer.get(jpeg);
                    ostream.write(jpeg);
                } catch (FileNotFoundException ex) {
                    Log.e(TAG, "Unable to open output file for writing", ex);
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to write the image to the output file", ex);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create a new output file", ex);
            } finally {
                mCapture.close();
            }
        }
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

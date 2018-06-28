/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;
import com.vuforia.samples.SampleApplication.SampleApplicationControl;
import com.vuforia.samples.SampleApplication.SampleApplicationException;
import com.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.vuforia.samples.SampleApplication.utils.SampleApplicationGLView;
import com.vuforia.samples.SampleApplication.utils.Texture;

import mx.com.lania.arblockly.Model.ARBModelo;
import mx.com.lania.arblockly.R;
import mx.com.lania.arblockly.utils.ARBCodeParse;
import mx.com.lania.arblockly.utils.ARBModel;
import mx.com.lania.arblockly.utils.CSVFile;


public class ImageTargets extends AppCompatActivity implements SampleApplicationControl {
    private static final String LOGTAG = "ImageTargets";
    
    SampleApplicationSession vuforiaAppSession;
    
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private int mStartDatasetsIndex = 0;
    private int mDatasetsNumber = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    
    // Objeto OpenGL
    private SampleApplicationGLView mGlView;
    
    // Renderizados
    private ImageTargetRenderer mRenderer;

    private GestureDetector mGestureDetector;
    private Vector<Texture> mTextures;
    
    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = true;
    private boolean mExtendedTracking = false;
    private View mFocusOptionView;
    private View mFlashOptionView;
    private RelativeLayout mUILayout;
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    private AlertDialog mErrorDialog;
    boolean mIsDroidDevice = false;

    // Elementos para el movimiento
    private ArrayList<ARBModel> arregloTarget1;
    private ArrayList<ARBModel> arregloTarget2;
    private ArrayList<ARBModel> arregloTarget3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // obtener los datos generados por los bloques
        Intent i = getIntent();
        String xml_result = i.getStringExtra("XML_RESULT");

        // Log.e(LOGTAG, xml_result);
        // generar las acciones con el analizador.
        ARBCodeParse parse =  new ARBCodeParse();
        parse.parse(xml_result);

        // obtener los arreglos de código
        this.arregloTarget1 = parse.getArregloTarget1();
        this.arregloTarget2 = parse.getArregloTarget2();
        this.arregloTarget3 = parse.getArregloTarget3();

        // inicio de la sesión de Vuforia
        vuforiaAppSession = new SampleApplicationSession(this);

        // carga de los datos
        startLoadingAnimation();
        mDatasetStrings.add("ARBlockly_t1.xml");
        // inicio del ciclo de vida
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // cargar las texturas
        mTextures = new Vector<Texture>();
        loadTextures();
    }
    
    // Process Single Tap event to trigger autofocus
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();
        
        
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }
        
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            boolean result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
            if (!result) Log.e("SingleTapUp", "Unable to trigger focus");
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    if (mContAutofocus) {
                        final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
                        if (!autofocusResult)
                            Log.e("SingleTapUp", "Unable to re-enable continuous auto-focus");
                    }
                }
            }, 1000L);
            return true;
        }
    }
    
    private void loadTextures() {
        /*mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("ImageTargets/Buildings.jpeg",
            getAssets()));*/
        //mTextures.add(Texture.loadTextureFromApk("ship.png", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("ship.png", getAssets()));

        // Cubo
        //mTextures.add(Texture.loadTextureFromApk("TextureP.png", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("TextureG.png", getAssets()));
        // Nave
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/ship2.bmp", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/ship2.bmp", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("CylinderTargets/EyesWhite.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/EyesWhite.jpg", getAssets()));

        // BB8
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/1t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/2t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/3t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/4t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/5t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/6t.jpg", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/4t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/7t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/8t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/9t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/10t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/11t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/12t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/13t.jpg", getAssets()));
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/t/14t.jpg", getAssets()));

        // Ping
        mTextures.add(Texture.loadTextureFromApk("CylinderTargets/p/penguin.bmp", getAssets()));
        // Nave
        mTextures.add(Texture.loadTextureFromApk("CylinderTargets/ship2.bmp", getAssets()));

        // Camion
        //mTextures.add(Texture.loadTextureFromApk("CylinderTargets/truck/truck_r.jpg", getAssets()));
    }
    
    
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Log.d(LOGTAG, "onResume");
        super.onResume();

        showProgressIndicator(true);
        if (mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        vuforiaAppSession.onResume();
    }
    

    @Override
    public void onConfigurationChanged(Configuration config) {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        vuforiaAppSession.onConfigurationChanged();
    }
    

    @Override
    protected void onPause() {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
        try {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }
    

    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ImageTargetRenderer(this, vuforiaAppSession);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
    }
    
    
    private void startLoadingAnimation() {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay, null);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
    }
    

    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();
        
        if (mCurrentDataset == null)
            return false;
        
        if (!mCurrentDataset.load(
            mDatasetStrings.get(mCurrentDatasetSelectionIndex),
            STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;
        
        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;
        
        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if(isExtendedTrackingActive()) {
                trackable.startExtendedTracking();
            }
            
            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data " + (String) trackable.getUserData());
        }
        return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData() {
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        if (mCurrentDataset != null && mCurrentDataset.isActive()) {
            if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset) && !objectTracker.deactivateDataSet(mCurrentDataset)) {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset)) {
                result = false;
            }
            
            mCurrentDataset = null;
        }
        
        return result;
    }

    @Override
    public void onVuforiaResumed() {
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted() {
        mRenderer.updateConfiguration();

        if (mContAutofocus) {
            if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
                if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)) {
                    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                }
            }
        }

        showProgressIndicator(false);
    }


    public void showProgressIndicator(boolean show) {
        if (loadingDialogHandler != null) {
            if (show) {
                loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            } else {
                loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }
    
    
    @Override
    public void onInitARDone(SampleApplicationException exception) {
        if (exception == null) {
            initApplicationAR();
            mRenderer.setActive(true);

            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mUILayout.bringToFront();

            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            
        } else {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }
    

    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ImageTargets.this);
                builder
                    .setMessage(errorMessage)
                    .setTitle(getString(R.string.INIT_ERROR))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton(getString(R.string.button_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    @Override
    public void onVuforiaUpdate(State state) {
        if (mSwitchDatasetAsap) {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker.getClassType());
            if (ot == null || mCurrentDataset == null || ot.getActiveDataSet(0) == null) {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }
            
            doUnloadTrackersData();
            doLoadTrackersData();
        }
    }
    
    
    @Override
    public boolean doInitTrackers() {
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Log.e(LOGTAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }
    
    
    @Override
    public boolean doStartTrackers() {
        boolean result = true;
        Tracker objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers() {
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers() {// Indicate if the trackers were deinitialized correctly
        boolean result = true;
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        return result;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    
    
    boolean isExtendedTrackingActive()
    {
        return mExtendedTracking;
    }
    
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    
    
    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ARBModel> getArregloTarget1() {
        return arregloTarget1;
    }

    public ArrayList<ARBModel> getArregloTarget2() {
        return arregloTarget2;
    }

    public ArrayList<ARBModel> getArregloTarget3() {
        return arregloTarget3;
    }
}

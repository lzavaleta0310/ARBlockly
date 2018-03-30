/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;
import com.vuforia.samples.SampleApplication.SampleAppRenderer;
import com.vuforia.samples.SampleApplication.SampleAppRendererControl;
import com.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.vuforia.samples.SampleApplication.utils.CubeObject;
import com.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.vuforia.samples.SampleApplication.utils.SampleApplication3DModel;
import com.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.vuforia.samples.SampleApplication.utils.Teapot;
import com.vuforia.samples.SampleApplication.utils.Texture;

import mx.com.lania.arblockly.utils.ARBModel;

public class ImageTargetRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl {
    private static final String LOGTAG = "ImageTargetRenderer";
    
    private SampleApplicationSession vuforiaAppSession;
    private ImageTargets mActivity;
    private SampleAppRenderer mSampleAppRenderer;

    private Vector<Texture> mTextures;
    
    private int shaderProgramID;
    private int vertexHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texSampler2DHandle;

    private CubeObject mCube;
    
    private float kBuildingScale = 0.012f;
    private SampleApplication3DModel mBuildingsModel;

    private boolean mIsActive = false;
    private boolean mModelIsLoaded = false;

    /**
     * Valor para escalar el  modelo
     * Cuando se trata de la tetera el valor para un modelo optimo es de 0.002f
     * En el caso del cubo, un valor adecuado es 0.015f
     */
    private static final float OBJECT_SCALE_FLOAT = 0.015f;
    private ArrayList<ARBModel> arregloTarget1;
    private int contadorMovimientosTarget1 = 0;
    
    public ImageTargetRenderer(ImageTargets activity, SampleApplicationSession session) {
        mActivity = activity;
        this.arregloTarget1 = mActivity.getArregloTarget1();
        vuforiaAppSession = session;
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f , 5f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;
        mSampleAppRenderer.render();
    }

    public void setActive(boolean active) {
        mIsActive = active;
        if(mIsActive)
            mSampleAppRenderer.configureVideoBackground();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        vuforiaAppSession.onSurfaceCreated();
        mSampleAppRenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        vuforiaAppSession.onSurfaceChanged(width, height);
        mSampleAppRenderer.onConfigurationChanged(mIsActive);

        initRendering();
    }
    
    
    // Function for initializing the renderer.
    private void initRendering() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);
        
        for (Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER, CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

        if(!mModelIsLoaded) {
            mCube = new CubeObject();

            try {
                mBuildingsModel = new SampleApplication3DModel();
                mBuildingsModel.loadModel(mActivity.getResources().getAssets(), "ImageTargets/Buildings.txt");
                mModelIsLoaded = true;
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable to load buildings");
            }

            // Hide the Loading Dialog
            mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        }

    }

    public void updateConfiguration()
    {
        mSampleAppRenderer.onConfigurationChanged(mIsActive);
    }

    public void renderFrame(State state, float[] projectionMatrix) {
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();
            Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            int textureIndex = trackable.getName().equalsIgnoreCase("cc") ? 0 : 1;

            float[] modelViewProjection = new float[16];

            if (state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("chips")) {
                if(mCube == null){
                    mCube = new CubeObject();
                }
                // Se traslada y escala el objeto para poder dibujarlo


                if(this.contadorMovimientosTarget1 < this.arregloTarget1.size()){
                    mCube.setPos_Y(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosY());
                    mCube.setPos_X(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosX());
                    mCube.setPos_Z(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosZ());
                    mCube.setPos_A(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosA());
                    mCube.setV_scale(this.arregloTarget1.get(this.contadorMovimientosTarget1).getvScale());
                    mCube.mover(modelViewMatrix);
                } else {
                    //Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, 0.015f);
                    Matrix.scaleM(modelViewMatrix, 0, mCube.getV_scale(), mCube.getV_scale(), mCube.getV_scale());
                    Matrix.translateM(modelViewMatrix, 0, mCube.getPos_X(), mCube.getPos_Y(), mCube.getPos_Z());
                }

                this.contadorMovimientosTarget1 += 1;
                // this.animateObject(modelViewMatrix);
            } else if(state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("cc")){
                //Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, 0.0f);
                //Matrix.scaleM(modelViewMatrix, 0, 0.0015f, 0.0015f, 0.0015f);
                //teapot.mover(modelViewMatrix);
            }

            Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

            GLES20.glUseProgram(shaderProgramID);

            if (!mActivity.isExtendedTrackingActive()) {
                if (state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("chips")) {
                    addTextures(textureIndex, modelViewProjection, 0);
                } else {
                    addTextures(textureIndex, modelViewProjection, 1);
                }
            } else {
                removeTextures(modelViewProjection);
            }

            SampleUtils.checkGLError("Render Frame");
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }


    public void addTextures(int textureIndex, float[] modelViewProjection, int tipe){
        if (tipe == 0){
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mCube.getVertices());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mCube.getTexCoords());

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
            GLES20.glUniform1i(texSampler2DHandle, 0);

            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

            // Se muestra el objeto finalmente
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mCube.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, mCube.getIndices());

            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
        } else if(tipe == 1) {
            /*GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, teapot.getVertices());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, teapot.getTexCoords());

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
            GLES20.glUniform1i(texSampler2DHandle, 0);

            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

            // Se muestra el objeto finalmente
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, teapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, teapot.getIndices());

            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);*/
        }

    }

    public void removeTextures(float[] modelViewProjection){
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mBuildingsModel.getVertices());
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mBuildingsModel.getTexCoords());

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(3).mTextureID[0]);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform1i(texSampler2DHandle, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mBuildingsModel.getNumObjectVertex());
    }
    
    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;
        
    }

    /*private double prevTime;
    private float rotateBallAngle;

    private void animateObject(float[] modelViewMatrix) {
        Log.e(LOGTAG, rotateBallAngle + " : valor");

        double time = System.currentTimeMillis();
        float dt = (float) (time - prevTime) / 1000;

        rotateBallAngle += dt * 180.0f / 3.1415f;
        rotateBallAngle %= 360;

        Matrix.rotateM(modelViewMatrix, 0, rotateBallAngle, 0.0f, 0.0f, 1.0f);

        prevTime = time;
    }*/
    
}

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

import mx.com.lania.arblockly.Model.Cube02;
import mx.com.lania.arblockly.Model.Rabbit;
import mx.com.lania.arblockly.Model.Ship;
import mx.com.lania.arblockly.utils.ARBModel;

public class ImageTargetRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl {
    private static final String LOGTAG = "ImageTargetRenderer";

    private SampleApplicationSession vuforiaAppSession;
    private ImageTargets mActivity;
    private SampleAppRenderer mSampleAppRenderer;

    private Vector<Texture> mTextures;

    private int shaderProgramID;
    private int vertexHandle;
    private int normalHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texSampler2DHandle;

    //private CubeObject mCube;
    //private CubeObject mCube;
    //private Ship mShip;
    //private SampleApplication3DModel mSphereModel;
    private SampleApplication3DModel modelo1;
    private SampleApplication3DModel modelo2;
    private SampleApplication3DModel modelo3;

    //private float kBuildingScale = 0.012f;
    private SampleApplication3DModel mBuildingsModel;

    private boolean mIsActive = false;
    private boolean mModelIsLoaded = false;

    /**
     * Valor para escalar el  modelo
     * Cuando se trata de la tetera el valor para un modelo optimo es de 0.002f
     * En el caso del cubo, un valor adecuado es 0.015f
     */
    //private static final float OBJECT_SCALE_FLOAT = 0.015f;
    private ArrayList<ARBModel> arregloTarget1;
    private ArrayList<ARBModel> arregloTarget2;
    private ArrayList<ARBModel> arregloTarget3;

    private int contadorMovimientosTarget1 = 0;
    private int contadorMovimientosTarget2 = 0;
    private int contadorMovimientosTarget3 = 0;

    public ImageTargetRenderer(ImageTargets activity, SampleApplicationSession session) {
        mActivity = activity;
        this.arregloTarget1 = mActivity.getArregloTarget1();
        this.arregloTarget2 = mActivity.getArregloTarget2();
        this.arregloTarget3 = mActivity.getArregloTarget3();

        vuforiaAppSession = session;
        //mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f , 5f);
        /***/
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.10f , 5f);
        /***/

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


    // inicializar el renderizado.
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
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

        if(!mModelIsLoaded) {
            //mCube = new CubeObject();
            //mShip = new Ship(mActivity.getCubeVertices(), mActivity.getCubeTexcoords(), mActivity.getCubeNormals());
            try {

                // cargar el modelo dependiendo del modelo definico en cada arreglo
                if(this.arregloTarget1.size() > 0){
                    switch (this.arregloTarget1.get(0).getTipo()){
                        case 1:
                            modelo1 = new SampleApplication3DModel();
                            modelo1.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/bb8a.txt");
                            break;
                        case 2:
                            modelo1 = new SampleApplication3DModel();
                            modelo1.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/baymax.txt");
                            break;
                        case 3:
                            modelo1 = new SampleApplication3DModel();
                            modelo1.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/p/ping.txt");
                            break;
                        case 4:
                            modelo1 = new SampleApplication3DModel();
                            modelo1.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/ship.txt");
                            break;
                    }
                }

                if(this.arregloTarget2.size() > 0) {
                    switch (this.arregloTarget2.get(0).getTipo()){
                        case 1:
                            modelo2 = new SampleApplication3DModel();
                            modelo2.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/bb8a.txt");
                            break;
                        case 2:
                            modelo2 = new SampleApplication3DModel();
                            modelo2.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/baymax.txt");
                            break;
                        case 3:
                            modelo2 = new SampleApplication3DModel();
                            modelo2.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/p/ping.txt");
                        case 4:
                            modelo2 = new SampleApplication3DModel();
                            modelo2.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/ship.txt");
                            break;
                    }
                }

                if(this.arregloTarget3.size() > 0) {
                    switch (this.arregloTarget3.get(0).getTipo()){
                        case 1:
                            modelo3 = new SampleApplication3DModel();
                            modelo3.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/bb8a.txt");
                            break;
                        case 2:
                            modelo3 = new SampleApplication3DModel();
                            modelo3.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/baymax.txt");
                            break;
                        case 3:
                            modelo3 = new SampleApplication3DModel();
                            modelo3.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/p/ping.txt");
                        case 4:
                            modelo3 = new SampleApplication3DModel();
                            modelo3.loadModel(mActivity.getResources().getAssets(), "CylinderTargets/ship.txt");
                            break;
                    }
                }
                mModelIsLoaded = true;
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable to load soccer ball");
            }

            try {
                mBuildingsModel = new SampleApplication3DModel();
                mBuildingsModel.loadModel(mActivity.getResources().getAssets(), "ImageTargets/Buildings.txt");
                mModelIsLoaded = true;
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable to load buildings");
            }

            // ocultar diálogo de carga
            mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        }

    }

    public void updateConfiguration() {
        mSampleAppRenderer.onConfigurationChanged(mIsActive);
    }

    public void renderFrame(State state, float[] projectionMatrix) {
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {

            if (state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("stones") && this.arregloTarget1.size() > 0) {
                TrackableResult result = state.getTrackableResult(tIdx);
                Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
                float[] modelViewProjection = new float[16];
                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

                // Conocer que target reconoce
                Trackable trackable = result.getTrackable();
                //int textureIndex = trackable.getName().equalsIgnoreCase("cc") ? 2 : 1;

                // iniciar el proceso de movimiento
                if(this.contadorMovimientosTarget1 < this.arregloTarget1.size()){
                    modelo1.setPos_Y(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosY());
                    modelo1.setPos_X(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosX());
                    modelo1.setPos_Z(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosZ());
                    modelo1.setPos_A(this.arregloTarget1.get(this.contadorMovimientosTarget1).getPosA());
                    modelo1.setV_scale(this.arregloTarget1.get(this.contadorMovimientosTarget1).getvScale());
                    modelo1.mover(modelViewMatrix);
                } else {
                    Matrix.translateM(modelViewMatrix, 0, modelo1.getPos_X(),modelo1.getPos_Y(),modelo1.getPos_Z());
                    Matrix.rotateM(modelViewMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
                    Matrix.rotateM(modelViewMatrix, 0, modelo1.getPos_A(), 0.0f, 1.0f, 0.0f);
                    Matrix.scaleM(modelViewMatrix, 0, modelo1.getV_scale(), modelo1.getV_scale(),modelo1.getV_scale());
                }
                this.contadorMovimientosTarget1 += 1;
                //animateObject(modelViewMatrix);

                Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glUseProgram(shaderProgramID);

                GLES20.glEnable(GLES20.GL_CULL_FACE);
                GLES20.glCullFace(GLES20.GL_BACK);

                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, modelo1.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, modelo1.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                if(this.arregloTarget1.size() > 0){
                    switch (this.arregloTarget1.get(0).getTipo()){
                        case 1:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(1).mTextureID[0]);
                            break;
                        case 2:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(0).mTextureID[0]);
                            break;
                        case 3:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(2).mTextureID[0]);
                            break;
                        case 4:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(3).mTextureID[0]);
                            break;
                    }
                }
                //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(0).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, modelo1.getNumObjectVertex());

                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);
            } else if(state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("chips") && this.arregloTarget2.size() > 0){
                TrackableResult result = state.getTrackableResult(tIdx);
                Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
                float[] modelViewProjection = new float[16];
                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

                // Conocer que target reconoce
                Trackable trackable = result.getTrackable();
                //int textureIndex = trackable.getName().equalsIgnoreCase("cc") ? 2 : 1;

                // iniciar el proceso de movimiento
                if(this.contadorMovimientosTarget2 < this.arregloTarget2.size()){
                    modelo2.setPos_Y(this.arregloTarget2.get(this.contadorMovimientosTarget2).getPosY());
                    modelo2.setPos_X(this.arregloTarget2.get(this.contadorMovimientosTarget2).getPosX());
                    modelo2.setPos_Z(this.arregloTarget2.get(this.contadorMovimientosTarget2).getPosZ());
                    modelo2.setPos_A(this.arregloTarget2.get(this.contadorMovimientosTarget2).getPosA());
                    modelo2.setV_scale(this.arregloTarget2.get(this.contadorMovimientosTarget2).getvScale());
                    modelo2.mover(modelViewMatrix);
                } else {
                    // valores muestra de inicialización
                    //Matrix.translateM(modelViewMatrix, 0, 0.02f,0.02f,0.10f);
                    //Matrix.rotateM(modelViewMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
                    //Matrix.rotateM(modelViewMatrix, 0, 90, 0.0f, 1.0f, 0.0f);
                    //Matrix.scaleM(modelViewMatrix, 0, 0.1f, 0.1f,0.1f);

                    Matrix.translateM(modelViewMatrix, 0, modelo2.getPos_X(),modelo2.getPos_Y(),modelo2.getPos_Z());
                    Matrix.rotateM(modelViewMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
                    Matrix.rotateM(modelViewMatrix, 0, modelo2.getPos_A(), 0.0f, 1.0f, 0.0f);
                    Matrix.scaleM(modelViewMatrix, 0, modelo2.getV_scale(), modelo2.getV_scale(),modelo2.getV_scale());
                }
                this.contadorMovimientosTarget2 += 1;

                Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glUseProgram(shaderProgramID);

                GLES20.glEnable(GLES20.GL_CULL_FACE);
                GLES20.glCullFace(GLES20.GL_BACK);

                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, modelo2.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, modelo2.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                if(this.arregloTarget2.size() > 0) {
                    switch (this.arregloTarget2.get(0).getTipo()){
                        case 1:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(1).mTextureID[0]);
                            break;
                        case 2:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(0).mTextureID[0]);
                            break;
                        case 3:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(2).mTextureID[0]);
                            break;
                        case 4:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(3).mTextureID[0]);
                            break;
                    }
                }
                //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(1).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, modelo2.getNumObjectVertex());

                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);
            } else if(state.getTrackableResult(tIdx).getTrackable().getName().equalsIgnoreCase("tarmac") && this.arregloTarget3.size() > 0){
                TrackableResult result = state.getTrackableResult(tIdx);
                Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
                float[] modelViewProjection = new float[16];
                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

                // Conocer que target reconoce
                Trackable trackable = result.getTrackable();
                //int textureIndex = trackable.getName().equalsIgnoreCase("cc") ? 2 : 1;

                // iniciar el proceso de movimiento
                if(this.contadorMovimientosTarget3 < this.arregloTarget3.size()){
                    modelo3.setPos_Y(this.arregloTarget3.get(this.contadorMovimientosTarget3).getPosY());
                    modelo3.setPos_X(this.arregloTarget3.get(this.contadorMovimientosTarget3).getPosX());
                    modelo3.setPos_Z(this.arregloTarget3.get(this.contadorMovimientosTarget3).getPosZ());
                    modelo3.setPos_A(this.arregloTarget3.get(this.contadorMovimientosTarget3).getPosA());
                    modelo3.setV_scale(this.arregloTarget3.get(this.contadorMovimientosTarget3).getvScale());
                    modelo3.mover(modelViewMatrix);
                } else {
                    Matrix.translateM(modelViewMatrix, 0, modelo3.getPos_X(),modelo3.getPos_Y(),modelo3.getPos_Z());
                    Matrix.rotateM(modelViewMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
                    Matrix.rotateM(modelViewMatrix, 0, modelo3.getPos_A(), 0.0f, 1.0f, 0.0f);
                    Matrix.scaleM(modelViewMatrix, 0, modelo3.getV_scale(), modelo3.getV_scale(),modelo3.getV_scale());
                }
                this.contadorMovimientosTarget3 += 1;

                Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);
                GLES20.glUseProgram(shaderProgramID);

                GLES20.glEnable(GLES20.GL_CULL_FACE);
                GLES20.glCullFace(GLES20.GL_BACK);

                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, modelo3.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, modelo3.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                if(this.arregloTarget3.size() > 0) {
                    switch (this.arregloTarget3.get(0).getTipo()){
                        case 1:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(1).mTextureID[0]);
                            break;
                        case 2:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(0).mTextureID[0]);
                            break;
                        case 3:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(2).mTextureID[0]);
                            break;
                        case 4:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(3).mTextureID[0]);
                            break;
                    }
                }
                //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(1).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, modelo3.getNumObjectVertex());

                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);
            }

            SampleUtils.checkGLError("Render Frame");
        }

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;
    }

    private double prevTime;
    private float rotateBallAngle;

    private void animateObject(float[] modelViewMatrix) {
        Log.e(LOGTAG, rotateBallAngle + " : valor");

        double time = System.currentTimeMillis();
        float dt = (float) (time - prevTime) / 1000;

        rotateBallAngle += dt * 180.0f / 3.1415f;
        rotateBallAngle %= 360;

        //Matrix.rotateM(modelViewMatrix, 0, rotateBallAngle, 0.0f, 0.0f, 1.0f);
        Log.e(LOGTAG, "angle :: " + rotateBallAngle);

        prevTime = time;
    }

}

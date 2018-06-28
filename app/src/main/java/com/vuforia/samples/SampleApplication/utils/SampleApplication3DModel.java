/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.SampleApplication.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.res.AssetManager;
import android.opengl.Matrix;
import android.util.Log;


public class SampleApplication3DModel extends MeshObject {
    // para modificar las posiciones
    private float pos_X = 0.02f;
    private float pos_Y = 0.02f;
    private float pos_Z = 0.11f;
    private float pos_A = 90.0f;
    private float v_scale = 0.1f;

    // necesarios para cargar los vertices
    private ByteBuffer verts;
    private ByteBuffer textCoords;
    private ByteBuffer norms;
    int numVerts = 0;
    
    
    public void loadModel(AssetManager assetManager, String filename) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            
            int floatsToRead = Integer.parseInt(line);
            numVerts = floatsToRead / 3;
            
            verts = ByteBuffer.allocateDirect(floatsToRead * 4);
            verts.order(ByteOrder.nativeOrder());
            for (int i = 0; i < floatsToRead; i++) {
                verts.putFloat(Float.parseFloat(reader.readLine()));
            }
            verts.rewind();
            
            line = reader.readLine();
            floatsToRead = Integer.parseInt(line);
            
            norms = ByteBuffer.allocateDirect(floatsToRead * 4);
            norms.order(ByteOrder.nativeOrder());
            for (int i = 0; i < floatsToRead; i++) {
                norms.putFloat(Float.parseFloat(reader.readLine()));
            }
            norms.rewind();
            
            line = reader.readLine();
            floatsToRead = Integer.parseInt(line);
            
            textCoords = ByteBuffer.allocateDirect(floatsToRead * 4);
            textCoords.order(ByteOrder.nativeOrder());
            for (int i = 0; i < floatsToRead; i++) {
                textCoords.putFloat(Float.parseFloat(reader.readLine()));
            }
            textCoords.rewind();
            
        } finally {
            if (is != null)
                is.close();
        }
    }

    // Método para animar el objeto
    public void mover(float[] modelViewMatrix){
        //Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, this.v_scale);
        Matrix.translateM(modelViewMatrix, 0, this.pos_X, this.pos_Y,this.pos_Z);
        Matrix.rotateM(modelViewMatrix, 0, 90, 1.0f, 0.0f, 0.0f);// vertical
        Matrix.rotateM(modelViewMatrix, 0, pos_A, 0.0f, 1.0f, 0.0f);// horizontal
        Matrix.scaleM(modelViewMatrix, 0, v_scale, v_scale, v_scale);
        //Matrix.rotateM(modelViewMatrix, 0, this.pos_A, 0.0f, 0.0f, 1.0f);
    }
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        Buffer result = null;
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                result = verts;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = textCoords;
                break;
            case BUFFER_TYPE_NORMALS:
                result = norms;
            default:
                break;
        }
        return result;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return numVerts;
    }
    
    
    @Override
    public int getNumObjectIndex()
    {
        return 0;
    }

    // accessors

    public float getPos_X() {
        return pos_X;
    }

    public void setPos_X(float pos_X) {
        this.pos_X = pos_X;
    }

    public float getPos_Y() {
        return pos_Y;
    }

    public void setPos_Y(float pos_Y) {
        this.pos_Y = pos_Y;
    }

    public float getPos_Z() {
        return pos_Z;
    }

    public void setPos_Z(float pos_Z) {
        this.pos_Z = pos_Z;
    }

    public float getPos_A() {
        return pos_A;
    }

    public void setPos_A(float pos_A) {
        this.pos_A = pos_A;
    }

    public float getV_scale() {
        return v_scale;
    }

    public void setV_scale(float v_scale) {
        this.v_scale = v_scale;
    }
}

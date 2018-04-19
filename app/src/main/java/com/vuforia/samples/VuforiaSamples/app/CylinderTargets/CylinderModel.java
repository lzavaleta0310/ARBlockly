/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.VuforiaSamples.app.CylinderTargets;

import android.opengl.Matrix;

import java.nio.Buffer;

import com.vuforia.samples.SampleApplication.utils.MeshObject;


public class CylinderModel extends MeshObject
{
    
    private int CYLINDER_NB_SIDES = 64;
    private int CYLINDER_NUM_VERTEX = ((CYLINDER_NB_SIDES * 2) + 2);
    private float mTopRadius;
    
    int indicesNumber;
    int verticesNumber;
    
    double cylinderVertices[];
    
    // 4 triangles per side, so 12 indices per side
    short cylinderIndices[];
    
    double cylinderTexCoords[];
    
    double cylinderNormals[];

    private float pos_X = 1.0f;
    private float pos_Y = 1.0f;
    private float pos_Z = 1.1f;
    private float pos_A = 1.0f;
    private float v_scale = 0.015f;

    // Método para animar el objeto
    public void mover(float[] modelViewMatrix){
        Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.scaleM(modelViewMatrix, 0, v_scale, v_scale, v_scale);

        Matrix.translateM(modelViewMatrix, 0, this.pos_X, this.pos_Y,this.pos_Z);
        Matrix.rotateM(modelViewMatrix, 0, this.pos_A, 0.0f, 0.0f, 1.0f);

    }

    // Accessors

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
    
    public CylinderModel(float topRadius)
    {
        cylinderVertices = new double[CYLINDER_NUM_VERTEX * 3];
        cylinderIndices = new short[CYLINDER_NB_SIDES * 12];
        cylinderTexCoords = new double[CYLINDER_NUM_VERTEX * 2];
        cylinderNormals = new double[CYLINDER_NUM_VERTEX * 3];
        mTopRadius = topRadius;
        prepareData();
        
        mVertBuff = fillBuffer(cylinderVertices);
        mTexCoordBuff = fillBuffer(cylinderTexCoords);
        mNormBuff = fillBuffer(cylinderNormals);
        mIndBuff = fillBuffer(cylinderIndices);
    }
    
    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;
        
        }
        
        return result;
    }
    
    
    void prepareData()
    {
        double deltaTex = (1.0 / (double) CYLINDER_NB_SIDES);
        
        // vertices index for the bottom and top vertex
        int ix_vertex_center_bottom = 2 * CYLINDER_NB_SIDES;
        int ix_vertex_center_top = ix_vertex_center_bottom + 1;
        
        for (int i = 0; i < CYLINDER_NB_SIDES; i++)
        {
            double angle = 2 * Math.PI * i / CYLINDER_NB_SIDES;
            
            // bottom circle
            cylinderVertices[(i * 3) + 0] = Math.cos(angle); // x
            cylinderVertices[(i * 3) + 1] = Math.sin(angle); // y
            cylinderVertices[(i * 3) + 2] = 0; // z
            
            // top circle
            cylinderVertices[(i + CYLINDER_NB_SIDES) * 3 + 0] = mTopRadius
                * cylinderVertices[i * 3 + 0]; // x
            cylinderVertices[(i + CYLINDER_NB_SIDES) * 3 + 1] = mTopRadius
                * cylinderVertices[i * 3 + 1]; // y
            cylinderVertices[(i + CYLINDER_NB_SIDES) * 3 + 2] = 1; // z
            
            // texture coordinates
            cylinderTexCoords[(i * 2) + 0] = i * deltaTex;
            cylinderTexCoords[(i * 2) + 1] = 1;
            
            cylinderTexCoords[((i + CYLINDER_NB_SIDES) * 2) + 0] = i * deltaTex;
            cylinderTexCoords[((i + CYLINDER_NB_SIDES) * 2) + 1] = 0;
            
            // normals
            
            cylinderNormals[(i * 3) + 0] = cylinderVertices[(i * 3) + 1];
            cylinderNormals[(i * 3) + 1] = -(cylinderVertices[(i * 3) + 0]);
            cylinderNormals[(i * 3) + 2] = 0; // z
            
            // top circle normals
            cylinderNormals[(i + CYLINDER_NB_SIDES) * 3 + 0] = mTopRadius
                * cylinderVertices[i * 3 + 1];
            cylinderNormals[(i + CYLINDER_NB_SIDES) * 3 + 1] = -(mTopRadius * cylinderVertices[i * 3 + 0]);
            cylinderNormals[(i + CYLINDER_NB_SIDES) * 3 + 2] = 0; // z
            
            // indices
            // triangles are b0 b1 t1 and t1 t0 b0 (bn: vertex #n on the bottom
            // circle, tn: vertex #n on the to circle)
            // i1 is the index of the next vertex - we wrap if we reach the end
            // of the circle
            int i1 = i + 1;
            if (i1 == CYLINDER_NB_SIDES)
            {
                i1 = 0;
            }
            cylinderIndices[(i * 12) + 0] = (short) i;
            cylinderIndices[(i * 12) + 1] = (short) i1;
            cylinderIndices[(i * 12) + 2] = (short) (i1 + CYLINDER_NB_SIDES);
            
            cylinderIndices[(i * 12) + 3] = (short) (i1 + CYLINDER_NB_SIDES);
            cylinderIndices[(i * 12) + 4] = (short) (i + CYLINDER_NB_SIDES);
            cylinderIndices[(i * 12) + 5] = (short) i;
            
            // bottom circle
            cylinderIndices[(i * 12) + 6] = (short) i1;
            cylinderIndices[(i * 12) + 7] = (short) i;
            cylinderIndices[(i * 12) + 8] = (short) ix_vertex_center_bottom;
            
            // top circle
            cylinderIndices[(i * 12) + 9] = (short) (i + CYLINDER_NB_SIDES);
            cylinderIndices[(i * 12) + 10] = (short) (i1 + CYLINDER_NB_SIDES);
            cylinderIndices[(i * 12) + 11] = (short) ix_vertex_center_top;
            
        }
        
        // we are adding 2 extra vertices: the center of each circle
        cylinderVertices[(3 * ix_vertex_center_bottom) + 0] = 0; // x
        cylinderVertices[(3 * ix_vertex_center_bottom) + 1] = 0; // y
        cylinderVertices[(3 * ix_vertex_center_bottom) + 2] = 0; // z
        
        cylinderVertices[(3 * ix_vertex_center_top) + 0] = 0; // x
        cylinderVertices[(3 * ix_vertex_center_top) + 1] = 0; // y
        cylinderVertices[(3 * ix_vertex_center_top) + 2] = 1; // z
        
        cylinderTexCoords[(ix_vertex_center_bottom * 2) + 0] = 0.5f;
        cylinderTexCoords[(ix_vertex_center_bottom * 2) + 1] = 0.5f;
        
        cylinderTexCoords[(ix_vertex_center_top * 2) + 0] = 0.5f;
        cylinderTexCoords[(ix_vertex_center_top * 2) + 1] = 0.5f;
        
        cylinderNormals[(3 * ix_vertex_center_bottom) + 0] = 0;
        cylinderNormals[(3 * ix_vertex_center_bottom) + 1] = 0;
        cylinderNormals[(3 * ix_vertex_center_bottom) + 2] = -1; // z
        
        cylinderNormals[(3 * ix_vertex_center_top) + 0] = 0;
        cylinderNormals[(3 * ix_vertex_center_top) + 1] = 0;
        cylinderNormals[(3 * ix_vertex_center_top) + 2] = 1; // z
        
        verticesNumber = cylinderVertices.length / 3;
        indicesNumber = cylinderIndices.length;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }
    
    
    @Override
    public int getNumObjectIndex()
    {
        return indicesNumber;
    }
}

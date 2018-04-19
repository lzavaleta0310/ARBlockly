package mx.com.lania.arblockly.Model;

import android.opengl.Matrix;

import com.vuforia.samples.SampleApplication.utils.MeshObject;

import java.nio.Buffer;

/**
 * Created by luis on 4/1/18.
 */

public class Cube02 extends MeshObject {
    private float pos_X = 1.0f;
    private float pos_Y = 1.0f;
    private float pos_Z = 1.1f;
    private float pos_A = 1.0f;
    private float v_scale = 0.015f;
    public static final int NUMVERTS = 36;

    // Método para animar el objeto
    public void mover(float[] modelViewMatrix){
        Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, this.v_scale);
        //Matrix.translateM(modelViewMatrix, 0, this.pos_X, this.pos_Y,this.pos_Z);
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

    // Data for drawing the 3D plane as overlay
    private static final double cubeVertices[]  = {
            0.499999812500094, -0.499999750000125, 0.499999625000188,
            -0.499999687500156, -0.499999750000125, -0.499999875000062,
            0.499999812500094, -0.499999750000125, -0.499999875000062,
            // f 8//2 6//2 5//2
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
            0.499999312500344, 0.499999750000125, 0.500000124999937,
            0.499999812500094, 0.499999750000125, -0.499999375000312,
            // f 5//3 2//3 1//3
            0.499999812500094, 0.499999750000125, -0.499999375000312,
            0.499999812500094, -0.499999750000125, 0.499999625000188,
            0.499999812500094, -0.499999750000125, -0.499999875000062,
            // f 6//4 3//4 2//4
            0.499999312500344, 0.499999750000125, 0.500000124999937,
            -0.499999687500156, -0.499999750000125, 0.499999625000188,
            0.499999812500094, -0.499999750000125, 0.499999625000188,
            // f 3//5 8//5 4//5
            -0.499999687500156, -0.499999750000125, 0.499999625000188,
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
            -0.499999687500156, -0.499999750000125, -0.499999875000062,
            // f 1//6 8//6 5//6
            0.499999812500094, -0.499999750000125, -0.499999875000062,
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
            0.499999812500094, 0.499999750000125, -0.499999375000312,
            // f 2//1 3//1 4//1
            0.499999812500094, -0.499999750000125, 0.499999625000188,
            -0.499999687500156, -0.499999750000125, 0.499999625000188,
            -0.499999687500156, -0.499999750000125, -0.499999875000062,
            // f 8//2 7//2 6//2
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
            -0.499999687500156, 0.499999750000125, 0.499999625000188,
            0.499999312500344, 0.499999750000125, 0.500000124999937,
            // f 5//3 6//3 2//3
            0.499999812500094, 0.499999750000125, -0.499999375000312,
            0.499999312500344, 0.499999750000125, 0.500000124999937,
            0.499999812500094, -0.499999750000125, 0.499999625000188,
            // f 6//4 7//4 3//4
            0.499999312500344, 0.499999750000125, 0.500000124999937,
            -0.499999687500156, 0.499999750000125, 0.499999625000188,
            -0.499999687500156, -0.499999750000125, 0.499999625000188,
            // f 3//5 7//5 8//5
            -0.499999687500156, -0.499999750000125, 0.499999625000188,
            -0.499999687500156, 0.499999750000125, 0.499999625000188,
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
            // f 1//6 4//6 8//6
            0.499999812500094, -0.499999750000125, -0.499999875000062,
            -0.499999687500156, -0.499999750000125, -0.499999875000062,
            -0.499999687500156, 0.499999750000125, -0.499999875000062,
    };


    private static final double cubeTexcoords[] = {
            0, 0, 1, 0, 1, 1, 0, 1,

            1, 0, 0, 0, 0, 1, 1, 1,

            0, 0, 1, 0, 1, 1, 0, 1,

            1, 0, 0, 0, 0, 1, 1, 1,

            0, 0, 1, 0, 1, 1, 0, 1,

            1, 0, 0, 0, 0, 1, 1, 1 };


    private static final double cubeNormals[]   = {
            // f 2//1 4//1 1//1
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            // f 8//2 6//2 5//2
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            // f 5//3 2//3 1//3
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            // f 6//4 3//4 2//4
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            // f 3//5 8//5 4//5
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            // f 1//6 8//6 5//6
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            // f 2//1 3//1 4//1
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            // f 8//2 7//2 6//2
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            // f 5//3 6//3 2//3
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            // f 6//4 7//4 3//4
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            // f 3//5 7//5 8//5
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            // f 1//6 4//6 8//6
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
    };


    private static final short  cubeIndices[]   = {
            0, 1, 2, 0, 2, 3, // front
            4, 6, 5, 4, 7, 6, // back
            8, 9, 10, 8, 10, 11, // left
            12, 14, 13, 12, 15, 14, // right
            16, 17, 18, 16, 18, 19, // top
            20, 22, 21, 20, 23, 22  // bottom
    };

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;


    public Cube02() {
        mVertBuff = fillBuffer(cubeVertices);
        mTexCoordBuff = fillBuffer(cubeTexcoords);
        mNormBuff = fillBuffer(cubeNormals);
        mIndBuff = fillBuffer(cubeIndices);
    }


    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        Buffer result = null;
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
            default:
                break;
        }
        return result;
    }


    @Override
    public int getNumObjectVertex()
    {
        return cubeVertices.length / 3;
    }


    @Override
    public int getNumObjectIndex()
    {
        return cubeIndices.length;
    }
}

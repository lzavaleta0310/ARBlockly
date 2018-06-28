package mx.com.lania.arblockly.utils;

/**
 * Created by luis on 3/29/18.
 */

public class ARBModel {
    private float posX;
    private float posY;
    private float posZ;
    private float posA;
    private float vScale;

    private int tipo;

    public ARBModel() {
    }

    /*public ARBModel(float posX, float posY, float posZ, float posA) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.posA = posA;
    }

    public ARBModel(float posX, float posY, float posZ, float posA, float vScale) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.posA = posA;
        this.vScale = vScale;
    }*/

    public ARBModel(float posX, float posY, float posZ, float posA, float vScale, int tipo) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.posA = posA;
        this.vScale = vScale;
        this.tipo = tipo;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public float getPosA() {
        return posA;
    }

    public void setPosA(float posA) {
        this.posA = posA;
    }

    public float getvScale() {
        return vScale;
    }

    public void setvScale(float vScale) {
        this.vScale = vScale;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}

package mx.com.lania.arblockly.Model;

import java.util.ArrayList;

import mx.com.lania.arblockly.utils.ARBModel;

public class ARBModelo {
    private int tipoModelo = 0;
    private int tipoTarget = 0;
    private ArrayList<ARBModel> arregloTarget1 = new ArrayList<ARBModel>();

    public ARBModelo() {}

    public ARBModelo(int tipoModelo, int tipoTarget, ArrayList<ARBModel> arregloTarget1) {
        this.tipoModelo = tipoModelo;
        this.tipoTarget = tipoTarget;
        this.arregloTarget1 = arregloTarget1;
    }

    public int getTipoModelo() {
        return tipoModelo;
    }

    public void setTipoModelo(int tipoModelo) {
        this.tipoModelo = tipoModelo;
    }

    public int getTipoTarget() {
        return tipoTarget;
    }

    public void setTipoTarget(int tipoTarget) {
        this.tipoTarget = tipoTarget;
    }

    public ArrayList<ARBModel> getArregloTarget1() {
        return arregloTarget1;
    }

    public void setArregloTarget1(ArrayList<ARBModel> arregloTarget1) {
        this.arregloTarget1 = arregloTarget1;
    }
}

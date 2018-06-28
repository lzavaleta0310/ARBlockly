package mx.com.lania.arblockly.utils;

import android.util.Log;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mx.com.lania.arblockly.Model.ARBModelo;

/**
 * Created by luis on 3/29/18.
 */

public class ARBCodeParse {
    private static final String TAG = "ARBCodeParse";

    private ArrayList<ARBModel> arregloTarget1 = new ArrayList<ARBModel>();
    private ArrayList<ARBModel> arregloTarget2 = new ArrayList<ARBModel>();
    private ArrayList<ARBModel> arregloTarget3 = new ArrayList<ARBModel>();

    // valores iniciales del modelo
    private float posX = 0.02f;
    private float posY = 0.02f;
    private float posZ = 0.12f;
    private float posA = 90;
    private float vScale = 0.1f;

    private ARBModel m;
    private int tipoTargetObjetivo = 0;

    /**
     *
     *
     */

    private int tipoTargetUso = 0;
    private int tipoModeloUso = 0;

    public void parse(String XML){
        try{

            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));

            Document doc = db.parse(is);
            NodeList listaNodos = doc.getElementsByTagName("block"); // Obtiene todos los bloques no importando el tipo

            for (int i = 0; i < listaNodos.getLength(); i++) {
                Element block = (Element) listaNodos.item(i);
                // Log.e(TAG, block.getAttribute("type"));

                // reconocer el tipo de target
                if(block.getAttribute("type").equalsIgnoreCase("arblock_target")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoTarget = (Element) listaField.item(listaField.getLength() - 1);
                    if (getCharacterDataFromElement(tipoTarget).equalsIgnoreCase("target_1")){
                        this.tipoTargetUso = 1;
                    }else if (getCharacterDataFromElement(tipoTarget).equalsIgnoreCase("target_2")){
                        this.tipoTargetUso = 2;
                    }else if (getCharacterDataFromElement(tipoTarget).equalsIgnoreCase("target_3")){
                        this.tipoTargetUso = 3;
                    }
                    this.restablecerValores();
                }

                // reconocer el tipo de modelo
                if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_1")){
                    this.tipoModeloUso = 1;
                } else if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_2")){
                    this.tipoModeloUso = 2;
                } else if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_3")){
                    this.tipoModeloUso = 3;
                } else if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_4")){
                    this.tipoModeloUso = 4;
                }


                // reconocer bloque movimiento
                if(block.getAttribute("type").equalsIgnoreCase("arblock_movimiento")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoMovimiento = (Element) listaField.item(0);
                    Element unidadesMovimiento = (Element) listaField.item(1);

                    if(getCharacterDataFromElement(tipoMovimiento).equalsIgnoreCase("adelante")){
                        this.movientoAdelante(getCharacterDataFromElement(unidadesMovimiento));
                    } else if(getCharacterDataFromElement(tipoMovimiento).equalsIgnoreCase("atras")){
                        this.movimientoAtras(getCharacterDataFromElement(unidadesMovimiento));
                    }
                }

                // reconocer bloque de giro
                if(block.getAttribute("type").equalsIgnoreCase("arblock_giro")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoGiro = (Element) listaField.item(0);

                    if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_derecha")){
                        this.movimientoDerecha();
                    } else if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_izquierda")){
                        this.movimientoIzquierda();
                    }
                }

                // reconocer bloque aumentar
                if(block.getAttribute("type").equalsIgnoreCase("arblock_aumentar")){
                    this.agrandarModelo();
                }

                // reconocer bloque disminuir
                if(block.getAttribute("type").equalsIgnoreCase("arblock_disminuir")){
                    this.encojerModelo();
                }
            }
        }catch (Exception e){
            Log.e(TAG, "Exception :: " + e.toString());
        }
    }

    public String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private void agrandarModelo(){
        float hasta = this.vScale + 0.010f;
        while (this.vScale < hasta){
            this.vScale = this.vScale + 0.0005f;
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale, this.tipoModeloUso);

            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
        }
    }

    private void encojerModelo(){
        float hasta = this.vScale - 0.010f;
        while (this.vScale > hasta){
            this.vScale = this.vScale - 0.0005f;
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloUso);

            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
        }
    }

    private void movientoAdelante(String unidades){
        float hasta = 0;
        switch (unidades){
            case "mov_uno":
                hasta = this.posX + 0.10f;
                break;
            case "mov_dos":
                hasta = this.posX + 0.20f;
                break;
            case "mov_tres":
                hasta = this.posX + 0.30f;
                break;
            case "mov_cuatro":
                hasta = this.posX + 0.40f;
                break;
            case "mov_cinco":
                hasta = this.posX + 0.50f;
                break;
            case "mov_seis":
                hasta = this.posX + 0.60f;
                break;
        }

        for(float x = this.posX; x < hasta; x = x + 0.001f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloUso);
            this.posX = x;

            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
        }
    }

    private void movimientoAtras(String unidades){
        float hasta = 0;
        switch (unidades){
            case "mov_uno":
                hasta = this.posX - 0.10f;
                break;
            case "mov_dos":
                hasta = this.posX - 0.20f;
                break;
            case "mov_tres":
                hasta = this.posX - 0.30f;
                break;
            case "mov_cuatro":
                hasta = this.posX - 0.40f;
                break;
            case "mov_cinco":
                hasta = this.posX - 0.50f;
                break;
            case "mov_seis":
                hasta = this.posX - 0.60f;
                break;
        }

        for(float x = this.posX; x > hasta; x = x - 0.001f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale, this.tipoModeloUso);
            this.posX = x;

            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
        }
    }

    private void movimientoDerecha(){
        float hasta = this.posA + 90;
        for(float a = this.posA; a < hasta; a = a + 1.0f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloUso);
            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
            this.posA = a;
        }
    }

    private void movimientoIzquierda(){
        float hasta = this.posA - 90;
        for(float a = this.posA; a > hasta; a = a - 1.0f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloUso);
            switch (tipoTargetUso){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
                case 3:
                    this.arregloTarget3.add(this.m);
                    break;
            }
            this.posA = a;
        }
    }

    // Accessors

    private void restablecerValores(){
        this.posX = 0.02f;
        this.posY = 0.02f;
        this.posZ = 0.12f;
        this.posA = 90;
        this.vScale = 0.1f;
    }

    public ArrayList<ARBModel> getArregloTarget1() {
        return arregloTarget1;
    }

    public void setArregloTarget1(ArrayList<ARBModel> arregloTarget1) {
        this.arregloTarget1 = arregloTarget1;
    }

    public ArrayList<ARBModel> getArregloTarget2() {
        return arregloTarget2;
    }

    public void setArregloTarget2(ArrayList<ARBModel> arregloTarget2) {
        this.arregloTarget2 = arregloTarget2;
    }

    public ArrayList<ARBModel> getArregloTarget3() {
        return arregloTarget3;
    }

    public void setArregloTarget3(ArrayList<ARBModel> arregloTarget3) {
        this.arregloTarget3 = arregloTarget3;
    }
}

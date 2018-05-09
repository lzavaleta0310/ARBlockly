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

/**
 * Created by luis on 3/29/18.
 */

public class ARBCodeParse {
    private static final String TAG = "ARBCodeParse";

    private ArrayList<ARBModel> arregloTarget1 = new ArrayList<ARBModel>();
    private ArrayList<ARBModel> arregloTarget2 = new ArrayList<ARBModel>();
    // Para el arreglo 1
    private float posX = 0;
    private float posY = 0;
    private float posZ = 0;
    private float posA = 0;
    private float vScale = 0.080f;
    // Para el arreglo 2
    private float posX2 = 0;
    private float posY2 = 0;
    private float posZ2 = 0;
    private float posA2 = 0;
    private float vScale2 = 0.080f;

    private ARBModel m;
    private int tipoTargetObjetivo = 0;
    private int tipoModeloRender = 0;

    public void parse(String XML){
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));

            Document doc = db.parse(is);
            NodeList listaNodos = doc.getElementsByTagName("block"); // Obtiene todos los bloques no importando el tipo

            for (int i = 0; i < listaNodos.getLength(); i++) {//Recorrer cada uno de los nodos blocks
                Element block = (Element) listaNodos.item(i);
                Log.e(TAG, block.getAttribute("type"));

                if(block.getAttribute("type").equalsIgnoreCase("arblock_target")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoTarget = (Element) listaField.item(listaField.getLength() - 1);
                    if (getCharacterDataFromElement(tipoTarget).equalsIgnoreCase("target_1")){
                        tipoTargetObjetivo = 1;
                    }else if (getCharacterDataFromElement(tipoTarget).equalsIgnoreCase("target_2")){
                        tipoTargetObjetivo = 2;
                    }
                }

                if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_1")){
                    tipoModeloRender = 1;
                } else if(block.getAttribute("type").equalsIgnoreCase("arblock_modelo_1")){
                    tipoModeloRender = 2;
                }

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

                if(block.getAttribute("type").equalsIgnoreCase("arblock_giro")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoGiro = (Element) listaField.item(0);
                    if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_derecha")){
                        this.movimientoDerecha();
                    } else if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_izquierda")){
                        this.movimientoIzquierda();
                    }
                }

                if(block.getAttribute("type").equalsIgnoreCase("arblock_aumentar")){
                    this.agrandarModelo();
                }

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
        switch (tipoTargetObjetivo){
            case 1:
                float hasta = this.vScale + 0.010f;
                while (this.vScale < hasta){
                    this.vScale = this.vScale + 0.0005f;
                    m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale, this.tipoModeloRender);
                    this.arregloTarget1.add(this.m);
                }
                break;
            case 2:
                float hasta2 = this.vScale2 + 0.010f;
                while (this.vScale2 < hasta2){
                    this.vScale2 = this.vScale2 + 0.0005f;
                    m = new ARBModel(this.posX2, this.posY2, this.posZ2, this.posA2, this.vScale2, this.tipoModeloRender);
                    this.arregloTarget2.add(this.m);
                }
                break;
        }
    }

    private void encojerModelo(){
        switch (tipoTargetObjetivo){
            case 1:
                float hasta = this.vScale - 0.010f;
                while (this.vScale > hasta){
                    this.vScale = this.vScale - 0.0005f;
                    m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloRender);
                    this.arregloTarget1.add(this.m);
                }
                break;
            case 2:
                float hasta2 = this.vScale2 - 0.010f;
                while (this.vScale2 > hasta2){
                    this.vScale2 = this.vScale2 - 0.0005f;
                    m = new ARBModel(this.posX2, this.posY2, this.posZ2, this.posA2, this.vScale2,this.tipoModeloRender);
                    this.arregloTarget2.add(this.m);
                }
                break;
        }


    }

    private void movientoAdelante(String unidades){
        switch (tipoTargetObjetivo){
            case 1:
                float hasta = 0;
                switch (unidades){
                    case "mov_uno":
                        hasta = this.posX + 1.0f;
                        break;
                    case "mov_dos":
                        hasta = this.posX + 2.0f;
                        break;
                    case "mov_tres":
                        hasta = this.posX + 3.0f;
                        break;
                    case "mov_cuatro":
                        hasta = this.posX + 4.0f;
                        break;
                    case "mov_cinco":
                        hasta = this.posX + 5.0f;
                        break;
                    case "mov_seis":
                        hasta = this.posX + 6.0f;
                        break;
                }

                for(float x = this.posX; x < hasta; x = x + 0.015f){
                    m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloRender);
                    this.arregloTarget1.add(this.m);
                    this.posX = x;
                }
                break;
            case 2:
                float hasta2 = 0;
                switch (unidades){
                    case "mov_uno":
                        hasta2 = this.posX2 + 1.0f;
                        break;
                    case "mov_dos":
                        hasta2 = this.posX2 + 2.0f;
                        break;
                    case "mov_tres":
                        hasta2 = this.posX2 + 3.0f;
                        break;
                    case "mov_cuatro":
                        hasta2 = this.posX2 + 4.0f;
                        break;
                    case "mov_cinco":
                        hasta2 = this.posX2 + 5.0f;
                        break;
                    case "mov_seis":
                        hasta2 = this.posX2 + 6.0f;
                        break;
                }

                for(float x = this.posX2; x < hasta2; x = x + 0.015f){
                    m = new ARBModel(this.posX2, this.posY2, this.posZ2, this.posA2, this.vScale2,this.tipoModeloRender);
                    this.arregloTarget2.add(this.m);
                    this.posX2 = x;
                }
                break;
        }
    }

    private void movimientoAtras(String unidades){
        float hasta = 0;
        switch (unidades){
            case "mov_uno":
                hasta = this.posX - 1.0f;
                break;
            case "mov_dos":
                hasta = this.posX - 2.0f;
                break;
            case "mov_tres":
                hasta = this.posX - 3.0f;
                break;
            case "mov_cuatro":
                hasta = this.posX - 4.0f;
                break;
            case "mov_cinco":
                hasta = this.posX - 5.0f;
                break;
            case "mov_seis":
                hasta = this.posX - 6.0f;
                break;
        }

        //float hasta = this.posX + (Float.parseFloat(unidades) * -1);
        for(float x = this.posX; x > hasta; x = x - 0.015f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloRender);
            switch (tipoTargetObjetivo){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
            }
            this.posX = x;
        }
    }

    private void movimientoDerecha(){
        float hasta = this.posA + 90;
        for(float a = this.posA; a < hasta; a = a + 0.50f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloRender);
            switch (tipoTargetObjetivo){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
            }
            this.posA = a;
        }
    }

    private void movimientoIzquierda(){
        float hasta = this.posA - 90;
        for(float a = this.posA; a > hasta; a = a - 0.50f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale,this.tipoModeloRender);
            switch (tipoTargetObjetivo){
                case 1:
                    this.arregloTarget1.add(this.m);
                    break;
                case 2:
                    this.arregloTarget2.add(this.m);
                    break;
            }
            this.posA = a;
        }
    }

    // Accessors

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
}

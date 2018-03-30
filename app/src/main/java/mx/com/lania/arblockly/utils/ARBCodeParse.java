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
    private static final String LOGTAG = "ARBCodeParse";

    private ArrayList<ARBModel> arregloTarget1 = new ArrayList<ARBModel>();
    private float posX = 0;
    private float posY = 0;
    private float posZ = 0;
    private float posA = 0;
    private float vScale = 0.015f;
    private ARBModel m;

    public void parse(String XML){
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));

            Document doc = db.parse(is);
            NodeList listaNodos = doc.getElementsByTagName("block");// Obtiene todos los bloques no importando el tipo
            Log.e(LOGTAG, "Número de nodos " + listaNodos.getLength());

            for (int i = 0; i < listaNodos.getLength(); i++) {//Recorrer cada uno de los nodos blocks
                Element block = (Element) listaNodos.item(i);
                Log.e(LOGTAG, block.getAttribute("type"));

                if(block.getAttribute("type").equalsIgnoreCase("arblock_mover")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoMovimiento = (Element) listaField.item(0);
                    Element unidadesMovimiento = (Element) listaField.item(1);

                    if(getCharacterDataFromElement(tipoMovimiento).equalsIgnoreCase("adelante")){
                        this.movientoAdelante(getCharacterDataFromElement(unidadesMovimiento));
                    } else if(getCharacterDataFromElement(tipoMovimiento).equalsIgnoreCase("atras")){
                        this.movimientoAtras(getCharacterDataFromElement(unidadesMovimiento));
                    }
                }

                if(block.getAttribute("type").equalsIgnoreCase("arblock_girar")){
                    NodeList listaField = block.getElementsByTagName("field");
                    Element tipoGiro = (Element) listaField.item(0);
                    if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_derecha")){
                        this.movimientoDerecha();
                    } else if(getCharacterDataFromElement(tipoGiro).equalsIgnoreCase("girar_izquierda")){
                        this.movimientoIzquierda();
                        //this.agrandarModelo("0.035");
                    }
                }

                if(block.getAttribute("type").equalsIgnoreCase("arblock_agrandar")){
                    //NodeList listaField = block.getElementsByTagName("field");
                    //Element unidadEscalamiento = (Element) listaField.item(0);
                    this.agrandarModelo("0.040");
                }
            }
        }catch (Exception e){

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

    private void agrandarModelo(String unidades){
        //this.vScale = 0.040f;
        //m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
        //this.arregloTarget1.add(this.m);

        float hasta = this.vScale + 0.010f;

        while (this.vScale < hasta){
            Log.e(LOGTAG, "valor agrandar " + this.vScale);
            this.vScale = this.vScale + 0.0005f;
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
            this.arregloTarget1.add(this.m);
        }
    }

    private void movientoAdelante(String unidades){
        float hasta = this.posX + Float.parseFloat(unidades);
        for(float x = this.posX; x < hasta; x = x + 0.015f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
            this.arregloTarget1.add(this.m);
            this.posX = x;
        }
    }

    private void movimientoAtras(String unidades){
        float hasta = this.posX + (Float.parseFloat(unidades) * -1);
        for(float x = this.posX; x > hasta; x = x - 0.015f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
            this.arregloTarget1.add(this.m);
            this.posX = x;
        }
    }

    private void movimientoDerecha(){
        float hasta = this.posA + 90;
        for(float a = this.posA; a < hasta; a = a + 0.50f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
            this.arregloTarget1.add(this.m);
            this.posA = a;
        }
    }

    private void movimientoIzquierda(){
        float hasta = this.posA - 90;
        for(float a = this.posA; a > hasta; a = a - 0.50f){
            m = new ARBModel(this.posX, this.posY, this.posZ, this.posA, this.vScale);
            this.arregloTarget1.add(this.m);
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
}

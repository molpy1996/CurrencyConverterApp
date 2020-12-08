package com.example.currencyconverterapp;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class DownloadCurrencyTask extends AsyncTask<String, Void, HashMap<String,Double>>{

    private static final String CURRENCY = "currency";
    private static final String CUBE_NODE = "//Cube/Cube/Cube";
    private static final String RATE = "rate";

    public URL currencyRateXML = null;

    public boolean connectionState;

    public boolean getConnectionState(){
        return connectionState;
    }

    @Override
    protected HashMap<String, Double> doInBackground(String... strings) {

        try {
            currencyRateXML = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionState = isConnectedToServer(currencyRateXML);

        HashMap<String, Double> updatedRates = parseXMLtoHashmap(currencyRateXML);

        return updatedRates;
    }

    public boolean isConnectedToServer(URL url){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected HashMap<String, Double> parseXMLtoHashmap(URL url){

        HashMap <String, Double> localRateTable = new HashMap<String, Double>();

        try {

            HttpURLConnection urlConnection = (HttpURLConnection) currencyRateXML.openConnection();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = currencyRateXML.openStream();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xPathString = CUBE_NODE; // CUBE_NODE= //Cube/Cube/Cube
            XPathExpression expr = xpath.compile(xPathString);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                NamedNodeMap attribs = node.getAttributes();
                if (attribs.getLength() > 0) {
                    Node currencyAttrib = attribs.getNamedItem(CURRENCY);
                    if (currencyAttrib != null) {
                        //if we can find  a key+value in the Hashmap updatedRates
                        String currencyTxt = currencyAttrib.getNodeValue();
                        Double rateValue =  Double.valueOf(attribs.getNamedItem(RATE).getNodeValue());
                        localRateTable.put(currencyTxt, rateValue);
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return localRateTable;
    }
}

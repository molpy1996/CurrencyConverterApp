package com.example.currencyconverterapp;
import android.os.AsyncTask;

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

    //public ArrayList<HashMap<String, Double>> rates = new ArrayList<HashMap<String, Double>>();
    public HashMap<String, Double> updatedRates = new HashMap<String, Double>();
    public URL currencyRateXML = null;

    public HashMap<String, Double> getUpdatedRates(){
        return this.updatedRates;
    }

    /*public void setUpdatedRates(String curr, Double rate){
        this.updatedRates.put(curr, rate);
    }*/

    @Override
    protected HashMap<String, Double> doInBackground(String... strings) {

        try {
            currencyRateXML = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxrefdaily.xml");


            this.updatedRates.putAll(this.parseXMLtoHashmap(currencyRateXML));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected HashMap<String, Double> parseXMLtoHashmap(URL url){

        HashMap<String, Double> localUpdatedRates = new HashMap<>();

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
                        //setUpdatedRates(currencyTxt, rateValue);
                        localUpdatedRates.put(currencyTxt, rateValue);
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return localUpdatedRates;
    }

    public Double getRatesbyId(String curr){
        for(HashMap.Entry<String, Double> entry : updatedRates.entrySet()) {
            if(entry.getKey().equals(curr))
                return entry.getValue();
            else
                continue;
        }
        return Double.valueOf(0);
    }
}

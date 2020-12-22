package com.example.currencyconverterapp;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static com.example.currencyconverterapp.DBManager.RatesListManager.RatesEntry.COLUMN_NAME;
import static com.example.currencyconverterapp.DBManager.RatesListManager.RatesEntry.COLUMN_RATE;
import static com.example.currencyconverterapp.DBManager.RatesListManager.RatesEntry.TABLE_NAME;

public class DownloadCurrencyTask extends AsyncTask<String, Void, HashMap<String,Double>>{

    private FBManager FBManagerWrite = new FBManager();
    private SQLiteDatabase DateBase;
    private Boolean DataDownloaded;
    private DBManager DBManager;
    public Activity activity = null;
    
    private static final String CURRENCY = "currency";
    private static final String CUBE_NODE = "//Cube/Cube/Cube";
    private static final String RATE = "rate";

    private URL currencyRateXML = null;
    private HashMap<String, Double> updatedRates;
    private boolean connectionState;

    public boolean getConnectionState(){
        return connectionState;
    }

    public DownloadCurrencyTask(Activity mainActivity) {
        this.activity = mainActivity;
    }
    @Override
    protected HashMap<String, Double> doInBackground(String... strings) {
        
        updatedRates = new HashMap<>();
        updatedRates.put("EUR", 1.0);

        try {
            currencyRateXML = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            DataDownloaded = true;

        } catch (IOException e) {
            e.printStackTrace();
            DataDownloaded = false;
        }

        connectionState = isConnectedToServer(currencyRateXML);

        DBManager = new DBManager(this.activity.getApplicationContext());

        // ONLINE MODE
        // if we can access "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
        //We fill our DB with the new data coming from the ECB website
        if (DataDownloaded) {

            //Write XML file into Hashmap
            updatedRates = parseXMLtoHashMap(currencyRateXML);
            
            //Write in the data base SQL
            DateBase = DBManager.getWritableDatabase();
            Iterator it = updatedRates.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry mapElement = (Map.Entry) it.next();
                ContentValues values = new ContentValues();
                //put the currency's name into the ContentValues object
                values.put(COLUMN_NAME, (String) mapElement.getKey());
                //put the currency's rate into the ContentValues object
                values.put(COLUMN_RATE, (Double) mapElement.getValue());
                //put the ContentValues containing the data into the DB
                DateBase.insert(TABLE_NAME, null, values);
                long newRowId = DateBase.insert(TABLE_NAME, null, values);
                Log.i("DB-ONLINE-MODE", Long.toString(newRowId));

                //now we can push our DB content to the firebase DB !
                FBManagerWrite.getInstance();
                int id = 0;
                Iterator it2 = updatedRates.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry mapElement2 = (Map.Entry) it2.next();
                    FBManagerWrite.writeNewRate(Integer.toString(id), (String) mapElement2.getKey(), (Double) mapElement2.getValue());
                    Log.i("DB-ONLINE-MODE", "DB to Firebase : "+it.toString());
                    id++;
                }
            }
        }

        //OFFLINE MODE
        //if we cannot access "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
        //we fill our HashMap with the data from the DB
        else{

            DateBase = DBManager.getReadableDatabase();
            Cursor cursor = DateBase.rawQuery("SELECT * FROM " +TABLE_NAME, null);
            Log.i("DB-OFFLINE-MODE", cursor.toString());
            if(cursor.moveToFirst()) {
                do {
                    updatedRates.put(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_RATE)));
                } while (cursor.moveToNext());
            }
            DateBase.close();
        }

        return updatedRates;
    }

    public boolean isConnectedToServer(URL url){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            DataDownloaded = true;
            return true;
        } catch (Exception e) {
            DataDownloaded = false;
            return false;
        }
    }

    protected HashMap<String, Double> parseXMLtoHashMap(URL url){

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
                        //if we can find  a couple of key&value in updatedRates
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

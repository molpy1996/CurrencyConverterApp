package com.example.currencyconverterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner currencies1;
    Spinner currencies2;
    EditText toConvert;
    String name;
    ArrayAdapter<CharSequence> adapter1;
    ArrayAdapter<CharSequence> adapter2;
    TextView result;
    Button button;

    double chosenRate;
    double value;
    double resultValue;
    public int chosenCurrency1 = 0;
    public int chosenCurrency2 = 0;

    public List<Currency> rateTable = new ArrayList<>();
    //public HashMap<String,Double> rateTable = new HashMap<String, Double>();

    //Hashmap linking currency name with currency symbol
    public HashMap<String, String> rateSymbol = new HashMap<>();

    DownloadCurrencyTask dlCurr;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rateSymbol = fillRateSymbol();

        toConvert = (EditText) findViewById(R.id.Lbutton);
        name = toConvert.getText().toString();

        result = (TextView) findViewById(R.id.Rbutton);

        button = (Button) findViewById(R.id.button);

        // Select the policy of the app
        // An other param is added in the AndroidManifest.xml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.button){

            Log.i("onClick", "Pushed the button");

            String strUserName = toConvert.getText().toString();
            if(TextUtils.isEmpty(strUserName)) {
                toConvert.setError("Please enter a number to convert");
                return;
            }
            if(!TextUtils.isDigitsOnly(toConvert.getText().toString())) {
                toConvert.setError("Please enter a number to convert");
                return;
            }

            /* EUR = 0 / USD = 1 / JPY = 2 / MXN = 3 */
            chosenCurrency1 = currencies1.getSelectedItemPosition();
            chosenCurrency2 = currencies2.getSelectedItemPosition();

            String chosenCurrencySymbol = (String) currencies2.getSelectedItem();
            Log.i("onClick", "chosenCurrencySymbol : "+getSymbol(chosenCurrencySymbol) );
            Log.i("onClick", "chosenCurrency1 : " + chosenCurrency1+" / chosenCurrency2 : " + chosenCurrency2);

            chosenRate = conversionRate();

            Log.i("onClick", "chosenRate value : "+chosenRate);

            value = Double.parseDouble(String.valueOf(toConvert.getText()));
            resultValue = value*chosenRate;

            //FIXME limit result to 2 decimals
            result.setText(String.valueOf(resultValue)+" "+getSymbol(chosenCurrencySymbol));
        }
    }

    @Override
    protected void onStart() {

        Log.i("onStart", "in onStart function");

        super.onStart();

        //TODO improve the UI of spinners
        currencies1 = (Spinner) findViewById(R.id.currencies_spinner1);
        currencies2 = (Spinner) findViewById(R.id.currencies_spinner2);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currenciesSpinner1, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter2 = ArrayAdapter.createFromResource(this,
                R.array.currenciesSpinner2, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the spinners
        currencies1.setAdapter(adapter1);
        currencies2.setAdapter(adapter2);

        dlCurr = new DownloadCurrencyTask();

        //filling the List<Currency> with Hashmap<String, Double> returned by the Asynctask
        try {
            for(HashMap.Entry<String, Double> entry : dlCurr.execute().get().entrySet()) {
                rateTable.add(new Currency(entry.getKey(), entry.getValue(), entry.getKey().toLowerCase(), getSymbolFromName(entry.getKey())));
                Log.i("onStart", "key : "+entry.getKey()+", value : "+entry.getValue());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("onStart", "end of onStart function");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dlCurr.getConnectionState() == true){
            Log.i("onResume", "connexion to BCE website done");
            Toast.makeText(this, "Updated rates from server !", Toast.LENGTH_LONG).show();
        }else{
            Log.i("onResume", "connexion to BCE website failed");
            Toast.makeText(this, "Cannot connect to server..", Toast.LENGTH_LONG).show();
        }

        button.setOnClickListener(this);

        Log.i("OnResume", "in onResume Function");
    }

    public String getSymbol(String currName){
        String symbol="";

        for(Currency currency : rateTable){
            if(currName.equals(currency.getName())){
                symbol = currency.getSymbol();
            }
        }
        return symbol;
    }

    public String getSymbolFromName(String currName){
        String symbol="";

        for(HashMap.Entry<String,String> entry : rateSymbol.entrySet()) {
            if (currName.equals(entry.getKey())) {
                symbol = entry.getValue();
            }
        }

        return symbol;
    }

    public HashMap<String, String> fillRateSymbol(){

        rateSymbol.put("EUR","€");
        rateSymbol.put("USD","$");
        rateSymbol.put("JPY","¥");
        rateSymbol.put("BGN","лв");
        rateSymbol.put("CZK","Kč");
        rateSymbol.put("DKK","kr");
        rateSymbol.put("GBP","£");
        rateSymbol.put("HUF","Ft");
        rateSymbol.put("PLN","zł");
        rateSymbol.put("RON","lei");
        rateSymbol.put("SEK","kr");
        rateSymbol.put("CHF","CHF");
        rateSymbol.put("ISK","kr");
        rateSymbol.put("NOK","kr");
        rateSymbol.put("HRK","kn");
        rateSymbol.put("RUB","₽");
        rateSymbol.put("TRY","TRY");
        rateSymbol.put("AUD","$");
        rateSymbol.put("BRL","R$");
        rateSymbol.put("CAD","$");
        rateSymbol.put("CNY","¥");
        rateSymbol.put("HKD","$");
        rateSymbol.put("IDR","Rp");
        rateSymbol.put("ILS","₪");
        rateSymbol.put("INR","INR");
        rateSymbol.put("KRW","₩");
        rateSymbol.put("MXN","₱");
        rateSymbol.put("MYR","RM");
        rateSymbol.put("NZD","$");
        rateSymbol.put("PHP","₱");
        rateSymbol.put("SGD","$");
        rateSymbol.put("THB","฿");
        rateSymbol.put("ZAR","R");

        return this.rateSymbol;
    }

    public double getRatesById(String curr){
        for(Currency currency : rateTable) {
            if(currency.getName().equals(curr))
                return currency.getRate();
            else
                continue;
        }
        return Double.valueOf(0);
    }

    //PSEUDO CODE
    // if same name => return rate = 1
    // else if spinner1 = EUR, find spinner2 in List<Currency> , return rate = List<Currency>(name of spinner 2)
    // else if spinner2 = EUR, find spinner1 in List<Currency> , return rate = 1/List<Currency>(name of spinner 1)
    // else find spinner1 & spinner2 in List<Currency>, return rate = [1/List<Currency>(name of spinner 1)] * List<Currency>(name of spinner 2)
    public double conversionRate(){
        Log.i("conversionRate", "in conversionRate function");

        double rate=0;

        String c1 = (String) currencies1.getSelectedItem();
        String c2 = (String) currencies2.getSelectedItem();

        Log.i("conversionRate", "c1 : "+c1+"c2 : "+c2);

        if(c1.equals(c2)){ rate = 1; }
        else if(c1.equals("EUR") && !c2.equals("EUR")){ rate = getRatesById(c2); }
        else if(!c1.equals("EUR") && c2.equals("EUR")){ rate = 1/getRatesById(c1); }
        else rate = (1/getRatesById(c1)) * getRatesById(c2) ;

        Log.i("conversionRate", "rate : "+rate);

        return rate;
    }

    public void ratesList(View view) {
        Log.i("ratesList", "pushed the floating button");

        Intent intent = new Intent(this, RateListActivity.class);

        //TODO verifier que Serializable fonctionne bien
        intent.putExtra("rateTable", (Serializable) rateTable);
        intent.putExtra("destCurr", chosenCurrency2);

        startActivity(intent);

        /*if(dlCurr.getConnectionState()) {
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_LONG).show();
        }*/
    }

}
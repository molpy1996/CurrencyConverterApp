package com.example.currencyconverterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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

    //TODO transform Hashmap<String, Double> to Hashmap<Currency>
    public HashMap<String,Double> rateTable = new HashMap<String, Double>();
    DownloadCurrencyTask dlCurr;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            double EURtoUSD = getRatesById("USD");
            double EURtoJPY = getRatesById("JPY");
            double EURtoMXN = getRatesById("MXN");

            Log.i("onClick", "EURtoUSD = " + String.valueOf(EURtoUSD));
            Log.i("onClick", "EURtoJPY = " + String.valueOf(EURtoJPY));
            Log.i("onClick", "EURtoMXN = " + String.valueOf(EURtoMXN));

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

           Log.i("onClick", "chosenCurrency1 : " + chosenCurrency1+" / chosenCurrency2 : " + chosenCurrency2);

            chosenRate = conversionRate(chosenCurrency1,chosenCurrency2);

            Log.i("onClick", "chosenRate value : "+chosenRate);

            value = Double.parseDouble(String.valueOf(toConvert.getText()));
            resultValue = value*chosenRate;

            result.setText(String.valueOf(resultValue));
        }
    }

    @Override
    protected void onStart() {

        Log.i("onStart", "in onStart function");

        super.onStart();

        dlCurr = new DownloadCurrencyTask();

        try {
            for(HashMap.Entry<String, Double> entry : dlCurr.execute().get().entrySet()) {
                rateTable.put(entry.getKey(), entry.getValue());
                Log.i("onStart", "key : "+entry.getKey()+", value : "+entry.getValue());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

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

    public double getRatesById(String curr){
        for(HashMap.Entry<String, Double> entry : rateTable.entrySet()) {
            if(entry.getKey().equals(curr))
                return entry.getValue();
            else
                continue;
        }
        return Double.valueOf(0);
    }

    public double conversionRate(int curr1, int curr2){
        Log.i("conversionRate", "in conversionRate function");

        double rate=0;
        double EURtoUSD = getRatesById("USD");
        double EURtoJPY = getRatesById("JPY");
        double EURtoMXN = getRatesById("MXN");


        //si les 2 monnaies sont les mêmes on ne fait rien (rate = 1)
        if(curr1==curr2){
            rate=1;
        }else if(curr1 == 0 && curr2 == 1) {
            //EURUSD
            rate = EURtoUSD;
        }
        else if(curr1 == 0 && curr2 == 2){
            //EURJPY
            rate = EURtoJPY;
        }else if(curr1 == 0 && curr2 == 3){
            //EURMXN
            rate = EURtoMXN;
        }else if(curr1 == 1 && curr2 == 0){
            //USDEUR
            rate = 1/EURtoUSD;
        }else if(curr1 == 1 && curr2 == 2){
            //USDJPY
            rate = (1/EURtoUSD) * EURtoJPY;
        }else if(curr1 == 1 && curr2 == 3){
            //USDMXN
            rate = (1/EURtoUSD) * EURtoMXN;
        }else if(curr1 == 2 && curr2 == 0){
            //JPYEUR
            rate = 1/EURtoJPY;
        }else if(curr1 == 2 && curr2 == 1){
            //JPYUSD
            rate = (1/EURtoJPY) * EURtoUSD;
        }else if(curr1 == 2 && curr2 == 3){
            //JPYMXN
            rate = (1/EURtoJPY) * EURtoMXN;
        }else if(curr1 == 3 && curr2 == 0){
            //MXNEUR
            rate = EURtoMXN;
        }else if(curr1 == 3 && curr2 == 1){
            //MXNUSD
            rate = (1/EURtoMXN) * EURtoUSD;
        }else if(curr1 == 3 && curr2 == 2){
            //MXNJPY
            rate = (1/EURtoMXN) * EURtoJPY;
        }
        return rate;
    }

    public void ratesList(View view) {
        Log.i("ratesList", "pushed the floating button");

        Intent intent = new Intent(this, RateListActivity.class);

        intent.putExtra("rateTable", rateTable);
        intent.putExtra("destCurr", chosenCurrency2);

        startActivity(intent);

        /*if(dlCurr.getConnectionState()) {
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_LONG).show();
        }*/
    }

}
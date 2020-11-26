package com.example.currencyconverterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner currencies1;
    Spinner currencies2;
    EditText toConvert;
    String name;
    ArrayAdapter<CharSequence> adapter;
    TextView result;
    Button button;
    double chosenRate;
    double value;
    double resultValue;
    int chosenCurrency1;
    int chosenCurrency2;



    public HashMap<String,Double> rateTable;

    public MainActivity() throws MalformedURLException {
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toConvert = (EditText) findViewById(R.id.Lbutton);
        name = toConvert.getText().toString();

        result = (TextView) findViewById(R.id.Rbutton);

        button = (Button) findViewById(R.id.button);

        rateTable = null;

        Log.i("onCreate", "after setOnClickListener");
    }

    public Double conversionRate(int curr1, int curr2){
        Log.i("conversionRate", "in conversionRate function");

        double rate=0;
        //si les 2 monnaies sont les mêmes on ne fait rien (rate = 1)
        if(curr1==curr2){
            rate=1;
        }else if(curr1 == 0 && curr2 == 1) {
            //EURUSD
            Log.i("conversionRate", "in EURUSD");

            rate = rateTable.get("USD");
        }
        else if(curr1 == 0 && curr2 == 2){
            //EURJPY
            rate = rateTable.get("JPY");
        }else if(curr1 == 0 && curr2 == 3){
            //EURMXN
            rate = rateTable.get("MXN");
        }else if(curr1 == 1 && curr2 == 0){
            //USDEUR
            rate = 1/rateTable.get("USD");
        }else if(curr1 == 1 && curr2 == 2){ //recommencer ici
            //USDJPY
            rate = (1/rateTable.get("USD")) * rateTable.get("JPY");
        }else if(curr1 == 1 && curr2 == 3){
            //USDMXN
            rate = (1/rateTable.get("USD")) * rateTable.get("MXN");
        }else if(curr1 == 2 && curr2 == 0){
            //JPYEUR
            rate = 1/rateTable.get("JPY");
        }else if(curr1 == 2 && curr2 == 1){
            //JPYUSD
            rate = (1/rateTable.get("JPY")) * rateTable.get("USD");
        }else if(curr1 == 2 && curr2 == 3){
            //JPYMXN
            rate = (1/rateTable.get("JPY")) * rateTable.get("MXN");
        }else if(curr1 == 3 && curr2 == 0){
            //MXNEUR
            rate = rateTable.get("MXN");
        }else if(curr1 == 3 && curr2 == 1){
            //MXNUSD
            rate = (1/rateTable.get("MXN")) * rateTable.get("USD");
        }else if(curr1 == 3 && curr2 == 2){
            //MXNJPY
            rate = (1/rateTable.get("MXN")) * rateTable.get("JPY");
        }
        return rate;
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

            /* EUR = 1 / USD = 2 / JPY = 3 */
            chosenCurrency1 = currencies1.getSelectedItemPosition();
            chosenCurrency2 = currencies2.getSelectedItemPosition();

            Log.i("onClick", "chosenCurrency1 : " + Integer.toString(chosenCurrency1));
            Log.i("onClick", "chosenCurrency2 : " + Integer.toString(chosenCurrency2));

            chosenRate = conversionRate(chosenCurrency1,chosenCurrency2);

            value = Double.parseDouble(String.valueOf(toConvert.getText()));
            resultValue = value*chosenRate;

            result.setText(String.valueOf(resultValue));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DownloadCurrencyTask dlCurr = new DownloadCurrencyTask();
        dlCurr.doInBackground();

        rateTable = dlCurr.getUpdatedRates();

        //updating background task that update the rates
        //dlCurr.execute();

        Log.i("onStart", "in onStart function");

        currencies1 = (Spinner) findViewById(R.id.currencies_spinner1);
        currencies2 = (Spinner) findViewById(R.id.currencies_spinner2);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.currenciesSpinner1, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        currencies1.setAdapter(adapter);
        currencies2.setAdapter(adapter);

        Log.i("onStart", "end of onStart function");
    }

    @Override
    protected void onResume() {
        super.onResume();

        button.setOnClickListener(this);

        Log.i("OnResume", "in onResume Function");
    }
}
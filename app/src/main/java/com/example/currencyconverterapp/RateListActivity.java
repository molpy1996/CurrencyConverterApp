package com.example.currencyconverterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateListActivity extends AppCompatActivity {

    private ListView ratesListView;
    private TextView rateElementView;

    private DataBaseManager dataBaseManager;

    //TODO populate arrays with other currencies symbol&mnemonic (https://www.xe.com/fr/symbols.php)
    static String[] currencySymbolList = {"€", "$", "¥", "₱"};
    //static String[] currencyMnemonicList = {"", "", "", ""euro", "dollar", "yen", "peso"};

    private  List <Currency> currencyList = new ArrayList<>();
    public HashMap<String, Double> updatedRates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        dataBaseManager = new DataBaseManager(this);

        Intent intent = getIntent();
        //FIXME change updatedRates from HashMap<String, Double> to List<Currency>
        HashMap<String, Double> updatedRates = (HashMap<String, Double>)intent.getSerializableExtra("rateTable");

        for(HashMap.Entry<String, Double> entry : updatedRates.entrySet()){
            dataBaseManager.insertRate(entry.getKey(), entry.getValue());
        }
        dataBaseManager.close();

        int destCurrency = (int) intent.getSerializableExtra("destCurr");
        //FIXME
        // currency rate to be modified according to destcurrency
        for(HashMap.Entry<String, Double> entry : updatedRates.entrySet()){
            currencyList.add(new Currency(entry.getKey(), entry.getValue(), entry.getKey().toLowerCase(), currencySymbolList[destCurrency]));
        }

        ratesListView = (ListView) findViewById(R.id.RatesListView);
        rateElementView = (TextView) findViewById(R.id.textViewRate);

        ratesListView.setAdapter(new CurrencyAdapter(this, currencyList));

    }

    public void modifyRate(View view){
        //TODO TP5 : modifying rates in OFF LINE mode
    }
}
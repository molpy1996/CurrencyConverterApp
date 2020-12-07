package com.example.currencyconverterapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends AppCompatActivity {

    public  List <Currency> currencyList = new ArrayList<>();
    public ListView ratesListView;
    public TextView rateElementView;

    static String[] currencySymbolList = {"€", "$", "¥", "₱"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        currencyList.add(new Currency("EUR", 1.0, "euro", "€"));
        currencyList.add(new Currency("USD", 2.0, "dollar", "€"));
        currencyList.add(new Currency("JPY", 3.0, "yen", "€"));
        currencyList.add(new Currency("MXN", 4.0, "peso", "€"));

        //TODO
        // boucle qui valorise toutes les currencies de currencyList avec symbol de chosenCurrency2
        // trouver le symbol avec chosenCurrency2 en index dans le tableau de symbol
        //Spinner spinner2 = findViewById(R.id.currencies_spinner2);
        //int destCurrency = spinner2.getSelectedItemPosition();

        for(Currency curr : currencyList){
            curr.setSymbol(currencySymbolList[0]);
        }

        ratesListView = (ListView) findViewById(R.id.RatesListView);
        rateElementView = (TextView) findViewById(R.id.textViewRate);

        //ArrayAdapter<String> RatesListAdapter = new ArrayAdapter<String>(this, R.layout.activity_rate_list, R.id.textViewRate, currencyList);
        ratesListView.setAdapter(new CurrencyAdapter(this, currencyList));

    }
}
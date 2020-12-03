package com.example.currencyconverterapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends AppCompatActivity {

    public  List <Currency> currencyList = new ArrayList<>();
    public ListView ratesListView;
    public TextView rateElementView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        currencyList.add(new Currency("EUR", 1.0, "euro"));
        currencyList.add(new Currency("USD", 2.0, "dollar"));
        currencyList.add(new Currency("JPY", 3.0, "yen"));
        currencyList.add(new Currency("MXN", 4.0, "peso"));


        ratesListView = (ListView) findViewById(R.id.RatesListView);
        rateElementView = (TextView) findViewById(R.id.textViewRate);

        //ArrayAdapter<String> RatesListAdapter = new ArrayAdapter<String>(this, R.layout.activity_rate_list, R.id.textViewRate, currencyList);
        ratesListView.setAdapter(new CurrencyAdapter(this, currencyList));

    }
}
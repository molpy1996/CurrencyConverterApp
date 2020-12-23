package com.example.currencyconverterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateListActivity extends AppCompatActivity {

    private ListView ratesListView;
    private TextView rateElementView;
    private FloatingActionButton returnButton;

    private  List <Currency> currencyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);


        Intent intent = getIntent();
        currencyList = (List<Currency>) intent.getSerializableExtra("currencyList");
        String destCurrencySymbol = (String) intent.getSerializableExtra("destCurrencySymbol");
        Boolean connectionState = (Boolean) intent.getSerializableExtra("connectionState");


        //symbol is set to chosenCurrency2
        for(Currency currency : currencyList){
            currency.setSymbol(destCurrencySymbol);
        }

        ratesListView = (ListView) findViewById(R.id.RatesListView);
        rateElementView = (TextView) findViewById(R.id.textViewRate);
        returnButton = (FloatingActionButton) findViewById(R.id.floatingReturnButton);

        ratesListView.setAdapter(new CurrencyAdapter(this, currencyList));

    }

    public void returnMain(View view) {
        Log.i("ReturnMain", "pushed the return to main activity button");

        Intent intent_back = new Intent(this, MainActivity.class);

        startActivity(intent_back);
    }
}
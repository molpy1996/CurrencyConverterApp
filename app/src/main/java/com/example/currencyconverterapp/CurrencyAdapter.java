package com.example.currencyconverterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends BaseAdapter {

    private Context context;
    private List<Currency> currencyList;
    private LayoutInflater inflater;

    public CurrencyAdapter(Context _context, List<Currency> _currencyList){
        this.context = _context;
        this.currencyList = _currencyList;
        this.inflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Currency getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.adapter_item, null);

        Currency currentCurrency = getItem(position);
        String currencyName = currentCurrency.getName();
        Double currencyRate = currentCurrency.getRate();
        String currencyMnemonic = currentCurrency.getMnemonic();

        TextView currencyNameView = convertView.findViewById(R.id.item_name);
        currencyNameView.setText(currencyName);

        TextView currencyRateView = convertView.findViewById(R.id.item_rate);
        String[] currencySymbol = {"€", "$", "¥", "₱"};
        currencyRateView.setText(currencyRate.toString() +" "+ currencySymbol[position]);

        ImageView currencyIconView = convertView.findViewById(R.id.item_icon);
        String resourceName = "item_"+currencyMnemonic+"_icon";
        int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        currencyIconView.setImageResource(resId);

        return convertView;
    }
}

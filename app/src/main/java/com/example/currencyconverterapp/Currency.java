package com.example.currencyconverterapp;

public class Currency {
    private String name;
    private double rate;
    private String mnemonic;

    public Currency(String _name, double _rate, String _mnemonic){
        this.name = _name;
        this.rate = _rate;
        this.mnemonic = _mnemonic;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }

    public String getMnemonic() {
        return mnemonic;
    }
}

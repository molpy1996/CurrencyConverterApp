package com.example.currencyconverterapp;

import java.io.Serializable;

public class Currency implements Serializable {
    private String name;
    private double rate;
    private String mnemonic;

    private String symbol;

    /*public Currency(String _name, double _rate){
        this.name = _name;
        this.rate = _rate;
    }*/

    public Currency(String _name, double _rate, String _mnemonic, String _symbol){
        this.name = _name;
        this.rate = _rate;
        this.mnemonic = _mnemonic;
        this.symbol = _symbol;
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

    public String getSymbol(){return symbol;}

}

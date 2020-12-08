package com.example.currencyconverterapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DataBaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbRates.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "create table T_Rates ("
                      + " idCurrency INTEGER PRIMARY KEY autoincrement,"
                      + " nameCurrency text not null,"
                      + " rateCurrency real not null"
                      + ")";

        //we execute the SQLite request
        db.execSQL(strSql);
        Log.i("DATABASE", "created SQLite table");
    }


    //invoked if DB version stored in the log files is not equal to the newVersion of the DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //TODO onUpgrade

        String strSql = "drop table T_Rates";
        this.onCreate(db);
        db.execSQL(strSql);

        Log.i("DATABASE", "upgraded table version");

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){

        //TODO onDowngrade

        String strSql = "drop table T_Rates";
        this.onCreate(db);
        db.execSQL(strSql);

        Log.i("DATABASE", "downgraded table version");
    }

    //to add 1 couple (currency , rate) to the DB
    public void insertRate(String nameCurrency, Double rateCurrency){

        nameCurrency = nameCurrency.replace("'", "''");

        String strSql = "insert into T_Rates ( nameCurrency , rateCurrency ) values ('"
                + nameCurrency + "', "+ rateCurrency + ")";

        this.getWritableDatabase().execSQL(strSql);

        Log.i("DATABASE", "added currency : " + nameCurrency + " with rate : " + rateCurrency);
    }
}

package com.example.currencyconverterapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBManager extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "T_rates.db";

    //a class that represents the structure of the database with the columns' names
    public final class RatesListManager {

        private RatesListManager() {}
        //defines the data base table contents
        public class RatesEntry implements BaseColumns {
            public static final String TABLE_NAME = "T_Rates";
            public static final String COLUMN_NAME = "currency";
            public static final String COLUMN_RATE = "rate";
        }
    }

    private static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + RatesListManager.RatesEntry.TABLE_NAME + " (" +
                    RatesListManager.RatesEntry._ID + " INTEGER PRIMARY KEY," +
                    RatesListManager.RatesEntry.COLUMN_NAME + " TEXT," +
                    RatesListManager.RatesEntry.COLUMN_RATE + " TEXT)";

    private static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + RatesListManager.RatesEntry.TABLE_NAME;


    public DBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
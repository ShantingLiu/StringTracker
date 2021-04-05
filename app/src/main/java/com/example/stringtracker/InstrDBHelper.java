package com.example.stringtracker;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class InstrDBHelper extends SQLiteOpenHelper {

    //Instruments
    private static final String DATABASE_NAME1 = "instruments";
    private static final int DATABASE_VERSION1 = 1;
    private static final String CREATE_TABLE_INSTRUMENTS =
            "create table instruments (InstrumentID integer primary key autoincrement, "
                    + "Brand text not null,"
                    + "Model text not null,"
                    + "InstrType text not null,"
                    + "Acoustic boolean not null,"
                    + "StringsID not null,"
                    + "InstallTimeStamp text not null,"
                    + "ChangeTimeStamp text not null,"
                    + "PlayTime integer not null,"
                    + "SessionCnt integer not null,"
                    + "CurrSessionStart text not null,"
                    + "LastSessionTime integer,"
                    + "SessionInProgress boolean);";

    public InstrDBHelper(@Nullable Context context){
        super(context, DATABASE_NAME1, null, DATABASE_VERSION1);
    }

    public InstrDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_INSTRUMENTS);
        } catch (SQLException e){
            System.out.println("### ERROR SQL Create Instruments Table");
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(StringTrackerDBHelper.class.getName(),
                "Upgrading database from version" + oldVersion + "to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS STRINGS ;");
        onCreate(db);

    }

}

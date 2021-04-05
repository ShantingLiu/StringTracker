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

public class StringsDBHelper extends SQLiteOpenHelper {

    //Strings Table
    private static final String DATABASE_NAME = "strings.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_STRINGS =
            "create table strings (StringsID integer primary key autoincrement, "
                    + "Brand text not null,"
                    + "Model text not null,"
                    + "Tension text not null,"
                    + "InstrType text ,"
                    + "Cost REAL not null,"
                    + "AvgLife REAL not null,"
                    + "ChangeCnt integer not null,"
                    + "FirstSession boolean not null,"
                    + "AvgProjStr text not null,"
                    + "AvgToneStr text not null,"
                    + "AvgIntonStr text not null)";

    public StringsDBHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public StringsDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_STRINGS);
        } catch (SQLException e){
            System.out.println("### ERROR SQL Create Strings Table");
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

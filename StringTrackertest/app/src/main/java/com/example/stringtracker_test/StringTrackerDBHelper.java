package com.example.stringtracker_test;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
public class StringTrackerDBHelper extends SQLiteOpenHelper {


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
                    + "AvgProjStr text not null,"
                    + "AvgToneStr text not null,"
                    + "AvgIntonStr text not null)";
    //Instruments
    private static final String DATABASE_NAME1 = "instruments.db";
    private static final int DATABASE_VERSION1 = 1;
    private static final String CREATE_TABLE_INSTRUMENTS =
            "create table strings (InstrumentID integer primary key autoincrement, "
                    + "Brand text not null,"
                    + "Model text not null,"
                    + "InstrType text not null,"
                    + "Acoustic boolean not null,"
                    + "StringsID not null,"
                    + "InstalTimeStamp text not null,"
                    + "ChangeTimeStamp text not null,"
                    + "PlayTime integer not null,"
                    + "SessionCnt integer not null,"
                    + "CurrentSessStart text not null,"
                    + "LastSessionTime integer,"
                    + "SessionInProgress boolean);";


    public StringTrackerDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INSTRUMENTS);
        db.execSQL(CREATE_TABLE_STRINGS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(StringTrackerDBHelper.class.getName(),
                "Upgrading database from version" + oldVersion + "to "
            + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS STRINGS");
        onCreate(db);

    }
}

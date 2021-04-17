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
import java.util.List;

// Instrument DB helper class AZ, WKD
public class InstrDBHelper extends SQLiteOpenHelper {

    //Instruments
    private static final String DATABASE_NAME1 = "instrumentsdb";
    private static final String KEY_INSTR_ID = "InstrumentID";
    private static final String KEY_BRAND = "Brand";
    private static final String KEY_MODEL = "Model";
    private static final String KEY_INSTRTYPE = "InstrType";
    private static final String KEY_PLAYTIME = "PlayTime";
    private static final String KEY_INSTALLTIME = "InstallTimeStamp";
    private static final int DATABASE_VERSION1 = 1;
    private static final String CREATE_TABLE_INSTRUMENTS =
            "create table "+DATABASE_NAME1+" (InstrumentID integer primary key autoincrement, "
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

    // constructor
    public InstrDBHelper(@Nullable Context context){
        super(context, DATABASE_NAME1, null, DATABASE_VERSION1);
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

    // Method to delete instrument record from DB
    public void delete(int instrID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DATABASE_NAME1+ " WHERE "+KEY_INSTR_ID+"="+instrID+"");
        db.close();
    }

    // Method to return a Hashmap (dictionary) list from the DB of all instruments with
    // data keys InstrID = "instrID" , Brand = "brand", Model = "model" in each hash item (note all values are String data type) wkd
    public ArrayList<HashMap<String, String>> getInstrList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> instrList = new ArrayList<>();
        String query = "SELECT "+KEY_INSTR_ID+", "+KEY_BRAND+", "+KEY_MODEL+", "+KEY_INSTRTYPE+ ", "+KEY_PLAYTIME+ ", "
                +KEY_INSTALLTIME+  " FROM "+ DATABASE_NAME1;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> instr = new HashMap<>();
            //instr.put("instrID",String.valueOf(Integer.parseInt(String.valueOf((cursor.getColumnIndex(KEY_INSTR_ID))))));
            instr.put("instrID",cursor.getString(cursor.getColumnIndex(KEY_INSTR_ID)));
            instr.put("brand",cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
            instr.put("model",cursor.getString(cursor.getColumnIndex(KEY_MODEL)));
            instr.put("type",cursor.getString(cursor.getColumnIndex(KEY_INSTRTYPE)));
            instr.put("playtime",cursor.getString(cursor.getColumnIndex(KEY_PLAYTIME)));
            instr.put("installtime",cursor.getString(cursor.getColumnIndex(KEY_INSTALLTIME)));

            instrList.add(instr);
        }
        db.close();
        return  instrList;
    }

    //  Method combines DB items into single string for ListView
    public ArrayList<String> getInstrStrList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> instrList = new ArrayList<>();
        String query = "SELECT "+KEY_INSTR_ID+", "+KEY_BRAND+", "+KEY_MODEL+", "+KEY_INSTRTYPE+ ", "+KEY_PLAYTIME+ ", "
                +KEY_INSTALLTIME+  " FROM "+ DATABASE_NAME1;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            String instr = new String();

            instr = "ID:"+cursor.getString(cursor.getColumnIndex(KEY_INSTR_ID))+" ("
                    +cursor.getString(cursor.getColumnIndex(KEY_INSTRTYPE))+") "
                    +cursor.getString(cursor.getColumnIndex(KEY_BRAND))+"-"
                    + cursor.getString(cursor.getColumnIndex(KEY_MODEL));
            instrList.add(instr);
        }
        db.close();
        return  instrList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(StringTrackerDBHelper.class.getName(),
                "Upgrading database from version" + oldVersion + "to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS INSTRUMENTSDB ;");
        onCreate(db);

    }

}

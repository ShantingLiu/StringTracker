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

// StringSets DB helper class  AZ, WKD
public class StringsDBHelper extends SQLiteOpenHelper {

    //Strings Table
    private static final String DATABASE_NAME = "stringsdb";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_STRINGS_ID = "StringsID";
    private static final String KEY_BRAND = "Brand";
    private static final String KEY_MODEL = "Model";
    private static final String KEY_TENSION = "Tension";
    private static final String KEY_INSTRTYPE = "InstrType";

    private static final String CREATE_TABLE_STRINGS =
            "create table "+DATABASE_NAME+" (StringsID integer primary key autoincrement, "
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
                    + "AvgIntonStr text not null);";

    public StringsDBHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

 //   public StringsDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
 //       super(context, name, factory, version);
 //   }

    // Method to return a Hashmap (dictionary) list from the DB of all stringsets with
    // data keys StringsID = "stringsID" , Brand = "brand", Model = "model" in each hash item (note all values are String data type) wkd
    public ArrayList<HashMap<String, String>> getStringsList(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> strList = new ArrayList<>();
        String query = "SELECT "+KEY_STRINGS_ID+", "+KEY_BRAND+", "+KEY_MODEL+", "+KEY_TENSION+", "+KEY_INSTRTYPE+" FROM "+ DATABASE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> str = new HashMap<>();
            System.out.println("*** DB strings ID="+cursor.getString(cursor.getColumnIndex(KEY_STRINGS_ID)));
            str.put("stringsID",cursor.getString(cursor.getColumnIndex(KEY_STRINGS_ID)));
            str.put("brand",cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
            str.put("model",cursor.getString(cursor.getColumnIndex(KEY_MODEL)));
            str.put("tension",cursor.getString(cursor.getColumnIndex(KEY_TENSION)));
            str.put("instrType",cursor.getString(cursor.getColumnIndex(KEY_INSTRTYPE)));

            strList.add(str);
        }
        return  strList;
    }

    // Method combines DB items into single string list for ListView
    public ArrayList< String> getStringsStrList(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> strList = new ArrayList<>();
        String query = "SELECT "+KEY_STRINGS_ID+", "+KEY_BRAND+", "+KEY_MODEL+", "+KEY_TENSION+", "+KEY_INSTRTYPE+" FROM "+ DATABASE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            String str = new String();
            //System.out.println("*** DB strings ID="+cursor.getString(cursor.getColumnIndex(KEY_STRINGS_ID)));

            str = "ID:"+cursor.getString(cursor.getColumnIndex(KEY_STRINGS_ID))+" ("
                    +cursor.getString(cursor.getColumnIndex(KEY_INSTRTYPE))+") "
                    +cursor.getString(cursor.getColumnIndex(KEY_BRAND))+"-"
                    +cursor.getString(cursor.getColumnIndex(KEY_MODEL));

            strList.add(str);
        }
        return  strList;
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
        db.execSQL("DROP TABLE IF EXISTS STRINGSDB ;");
        onCreate(db);

    }

}

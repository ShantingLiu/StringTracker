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

    public StringTrackerDBHelper(@Nullable Context context){
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

/*  // EXAMPLE SQLite QUERY code
    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_Users;
        Cursor cursor = db.query(TABLE_Users, new String[]{KEY_NAME, KEY_LOC, KEY_DESG}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("designation",cursor.getString(cursor.getColumnIndex(KEY_DESG)));
            user.put("location",cursor.getString(cursor.getColumnIndex(KEY_LOC)));
            userList.add(user);
        }
        return  userList;
    }
*/

}



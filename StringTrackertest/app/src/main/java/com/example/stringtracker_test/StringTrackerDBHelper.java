package com.example.stringtracker_test;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
public class StringTrackerDBHelper extends SQLiteOpenHelper {

    private final String DELIM = "; ";  // delimiter for state passing data

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

    public void writeStrings(String strset, boolean newset){
        if (strset != null) {
            String tokens[] = strset.split(DELIM.trim());
        // TODO add DB record write of StringSet data
       /*   if(newset){
                // gen new StringsID from DB
            } else {
                // use ID given
                StringsID = Integer.parseInt(tokens[0].trim());
            }
            Brand = tokens[1].trim();
            Model = tokens[2].trim();
            Type = tokens[3].trim();
            Cost = Float.parseFloat(tokens[4].trim());
            AvgLife = Integer.parseInt(tokens[5].trim());
            ChangeCnt = Integer.parseInt(tokens[6].trim());
            FirstSession = Boolean.parseBoolean(tokens[7].trim());
            AvgProjStr = tokens[8].trim();
            AvgToneStr = tokens[9].trim();
            AvgIntonStr = tokens[10].trim();   */
        }
    }


    public void writeInstr(String instr, boolean newset){
        if (instr != null) {
            String tokens[] = instr.split(DELIM.trim());
         // TODO add DB record write of Instrument data
        /*  if(newset){
               // gen new InstrID from DB
            } else {
                // use ID given
                InstrID = Integer.parseInt(tokens[0].trim());
            }
            Brand = tokens[1].trim();
            Model = tokens[2].trim();
            Type = tokens[3].trim();
            LastSessionTime = Integer.parseInt(tokens[4].trim());
            Acoustic = Boolean.parseBoolean(tokens[5].trim());
            StringsID = Integer.parseInt(tokens[6].trim());
            InstallTimeStamp = tokens[7].trim();
            ChangeTimeStamp = tokens[8].trim();
            PlayTime = Integer.parseInt(tokens[9].trim());
            SessionCnt = Integer.parseInt(tokens[10].trim());
            CurrSessionStart = tokens[11].trim();
            SessionInProgress = Boolean.parseBoolean(tokens[12].trim()); */
        }
    }


    public String readStrings(int StrID) {
        String outstr =  null;  // remove null when done
        // TODO add DB read to fill these values
          /*      String.valueOf(StringsID)+DELIM+
                        String.valueOf(Brand)+DELIM+
                        String.valueOf(Model)+DELIM+
                        String.valueOf(Type)+DELIM+
                        String.valueOf(Cost)+DELIM+
                        String.valueOf(AvgLife)+DELIM+
                        String.valueOf(ChangeCnt)+DELIM+
                        String.valueOf(FirstSession)+DELIM+
                        String.valueOf(AvgProjStr)+DELIM+
                        String.valueOf(AvgToneStr)+DELIM+
                        String.valueOf(AvgIntonStr); */
        return outstr;
    }

    public String readInstr(int StrID) {
        String outstr = null;  // remove null when done
        // TODO add DB read to fill these values
              /*  String.valueOf(InstrID)+DELIM+
                        String.valueOf(Brand)+DELIM+
                        String.valueOf(Model)+DELIM+
                        String.valueOf(Type)+DELIM+
                        String.valueOf(LastSessionTime)+DELIM+
                        String.valueOf(Acoustic)+DELIM+
                        String.valueOf(StringsID)+DELIM+
                        String.valueOf(InstallTimeStamp)+DELIM+
                        String.valueOf(ChangeTimeStamp)+DELIM+
                        String.valueOf(PlayTime)+DELIM+
                        String.valueOf(SessionCnt)+DELIM+
                        CurrSessionStart+DELIM+
                        String.valueOf(SessionInProgress);  */
        return outstr;
    }



}



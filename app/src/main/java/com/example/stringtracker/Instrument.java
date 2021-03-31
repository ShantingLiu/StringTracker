package com.example.stringtracker;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Instrument data class and supporting functions  WKD 3-21-21
public class Instrument {
    private  int InstrID;
    private String Brand;
    private String Model;
    private String Type;
    private  boolean Acoustic;   // true = acoustic, false = electric
    private  int StringsID;      // ref to current strings selected
    private String InstallTimeStamp;    // install date
    private String ChangeTimeStamp;     // change date - updated upon string change event
    private  int PlayTime;               // number of total minutes played on set
    private  int SessionCnt;             // number of sessions on this set
    private String CurrSessionStart;    // timestamp for start of current session
    private  int LastSessionTime;        // number of minutes in last session (for undo)
    private  boolean SessionInProgress;  // set true at start of a session, false when stopped

    //private final String path = "data/";   //path to data files
    private final String path = Environment.getExternalStorageDirectory().getPath()+"/Documents/StringTrackerData/";
    private final String DELIM = "; ";  // delimiter for state passing data

    public String filename;

    private SQLiteDatabase database;
    StringTrackerDBHelper dbHelper;


    // constructor default values
    Instrument(){
        InstrID = 0;
        Brand = "";
        Model = "";
        Type = "";
        Acoustic = true;
        StringsID = 0;
        InstallTimeStamp = "";
        ChangeTimeStamp = "";
        PlayTime = 0;
        SessionCnt = 0;
        CurrSessionStart = "";
        LastSessionTime = 0;
        SessionInProgress = false;
    }
    // constructor
    Instrument(int i, String br, String mod, String ty, boolean ac, int sid, String its,
               String cts, int pt, int sc, String css, int lst, boolean sip){
        InstrID = i;
        Brand = br;
        Model = mod;
        Type = ty;
        Acoustic = ac;
        StringsID = sid;
        InstallTimeStamp = its;
        ChangeTimeStamp = cts;
        PlayTime = pt;
        SessionCnt = sc;
        CurrSessionStart = css;
        LastSessionTime = lst;
        SessionInProgress = sip;
    }
    // constructor
    Instrument(Instrument x){
        InstrID = x.getInstrID();
        Brand = x.getBrand();
        Model = x.getModel();
        Type = x.getType();
        Acoustic = x.getAcoustic();
        StringsID = x.getStringsID();
        InstallTimeStamp = x.getInstallTimeStamp();
        ChangeTimeStamp = x.getChangeTimeStamp();
        PlayTime = x.getPlayTime();
        SessionCnt = x.getSessionCnt();
        CurrSessionStart = x.getCurrSessionStart();
        LastSessionTime = x.getLastSessionTime();
        SessionInProgress = false;
    }

    // init method run for each new string cycle
    void init() {
        PlayTime = 0;
        SessionCnt = 0;
        InstallTimeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    }

    // Method deletes present SentLog file use this after storing and updating string change info
    void clearSentLog() throws IOException {
        String filename = "SentLog"+Brand+Model+InstrID+".dat";
        File data = new File(path+filename);
        data.delete();
    }

    // Method returns a string of Instr State data using comma delimiters
    public String getInstState(){
            String outstr =
                String.valueOf(InstrID)+DELIM+
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
                        String.valueOf(SessionInProgress);
        return outstr;
    }


    // Method to set Instr State parameters from a string using comma delimiters
    public void setInstState(String line){
        if (line != null) {
            String tokens[] = line.split(DELIM.trim());

            InstrID = Integer.parseInt(tokens[0].trim());
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
            SessionInProgress = Boolean.parseBoolean(tokens[12].trim());
        }
    }

    // method to store user sentiment in log file
    void logSessionSent(SessionSent s) {
        String filename = "SentLog"+Brand+Model+InstrID+".dat";
        String delim = ", ";
        boolean appendflag;

        if(SessionCnt == 0) {       // overwrite session sentiment file only if first session
            appendflag = false;
            System.out.print(appendflag);
        } else {
            appendflag = true;
        }

        try {
            FileWriter filewriter = new FileWriter(path+filename, appendflag); // true = appends to file
            PrintWriter outfile = new PrintWriter(filewriter);
            String outstr =
                    s.getTimeStamp()+delim
                            + String.valueOf(s.getSessTime())+delim
                            + Float.toString(s.getProj())+delim
                            + Float.toString(s.getTone())+delim
                            + Float.toString(s.getInton());

            outfile.println(outstr);
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method to read in SentLog file entries and return a List of SessionSent items
    public List<SessionSent> getSentLog() {
        List<SessionSent> rdata = new ArrayList<SessionSent>();
        try {
            filename = "SentLog"+Brand+Model+InstrID+".dat";   /// fix!!!
            File data = new File(path+filename);
            InputStream f = new FileInputStream(data);
            BufferedReader br = new BufferedReader(new InputStreamReader(f));
            //read a line, parse by commas, then store data tokens produced
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                String tokens[] = line.split(",");
                rdata.add( new SessionSent(
                        tokens[0],
                        Integer.parseInt(tokens[1].trim()),
                        Float.parseFloat(tokens[2].trim()),
                        Float.parseFloat(tokens[3].trim()),
                        Float.parseFloat(tokens[4].trim())
                ));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rdata;
    }

    // Method to store instrument data upon string change event
    // does not affect present state of instrument data. This must be reset separately.
    void logStringChange(){
        String filename = "ChangeLog"+Brand+Model+InstrID+".dat";
        String delim = ", ";
        ChangeTimeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        try {
            FileWriter filewriter = new FileWriter(path+filename, true);	// true = appends to file
            PrintWriter outfile = new PrintWriter(filewriter);

            String outstr =
                    String.valueOf(InstrID)+delim
                            +Brand+delim
                            +Model+delim
                            +Type+delim
                            + String.valueOf(Acoustic)+delim
                            + String.valueOf(StringsID)+delim
                            +InstallTimeStamp+delim           // timestamp installed
                            +ChangeTimeStamp+delim            // timestamp changed
                            + String.valueOf(PlayTime)+delim
                            + String.valueOf(SessionCnt);
            outfile.println(outstr);
            outfile.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        //init(); // <--<<< resets SessionCnt and PlayTime upon string change here????
    }

    // Method to read in ChangeLog file entries and return a List of Instrument items
    // *** presently uses this same class but should probably have its own subclass
    public List<Instrument> getChangeLog() {
        List<Instrument> rdata = new ArrayList<Instrument>();
        try {
            String filename = "ChangeLog"+Brand+Model+InstrID+".dat";
            File data = new File(path+filename);
            InputStream f = new FileInputStream(data);
            BufferedReader br = new BufferedReader(new InputStreamReader(f));
            //read a line, parse by commas, then store data tokens produced
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                String tokens[] = line.split(",");
                rdata.add( new Instrument(
                        Integer.parseInt(tokens[0].trim()),       // InstrID
                        tokens[1].trim(),                         // Brand
                        tokens[2].trim(),                         // Model
                        tokens[3].trim(),                         // Type
                        Boolean.parseBoolean(tokens[4].trim()),   // Acoustic
                        Integer.parseInt(tokens[5].trim()),       // StringsID
                        tokens[6],         							// InstallTimeStamp
                        tokens[7],                                // ChangeTimeStamp
                        Integer.parseInt(tokens[8].trim()),       // PlayTime
                        Integer.parseInt(tokens[9].trim()),       // SessionCnt
                        "",                                       // CurrSessStart-placeholder
                        0,            	                         // LastSessionTime-placeholder
                        false                                     // SessionInProgress-placeholder
                ));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rdata;
    }

//////////////////////// DB //////////////////////////////////

    public void open() throws SQLException {
     }

    public void close() {

    }

    public boolean insertInstr() throws SQLException {   //Rating???
        boolean didSucceed = false;
        database = dbHelper.getWritableDatabase();

        try {
            ContentValues initialValues = new ContentValues();

            //  Note that InstrID is automatically generated by the db
            initialValues.put("Brand", Brand);
            initialValues.put("Model", Model);
            initialValues.put("InstrType", Type);
            initialValues.put("LastSessionTime", LastSessionTime);
            initialValues.put("Acoustic", Acoustic);
            initialValues.put("StringsID", StringsID);
            initialValues.put("InstallTimeStamp", InstallTimeStamp);
            initialValues.put("ChangeTimeStamp", ChangeTimeStamp);
            initialValues.put("PlayTime", PlayTime);
            initialValues.put("SessionCnt", SessionCnt);
            initialValues.put("SessionInProgress", SessionInProgress);
            initialValues.put("CurrSessionStart", CurrSessionStart);

            didSucceed = database.insert("instruments.db", null, initialValues) > 0;
            dbHelper.close();
        }
        catch (Exception e) {
            Log.d("INSTRUMENT", "Errror inserting in instruments.db");
            //Do nothing -will return false if there is an exception
        }
        return didSucceed;
    }

/*    public boolean updateRating(MealRating c) {
        boolean didSucceed = false;
        try {
            Long rowId = (long) c.getRatingID();
            ContentValues updateValues = new ContentValues();  //?

            updateValues.put("restaurant", c.getRestaurant());
            updateValues.put("dish", c.getDish());
            updateValues.put("rating", c.getRating());

            didSucceed = database.update("contact", updateValues, "_id=" + rowId, null) > 0;
        }
        catch (Exception e) {
            //Do nothing -will return false if there is an exception
        }
        return didSucceed;
    }

    public int getLastRating() {
        int lastId;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        }
        catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }
*/
////////////////////////////////////////////////////////////////////

    // getters
    int getInstrID() {
        return InstrID;
    }
    String getBrand() {
        return Brand;
    }
    String getModel() {
        return Model;
    }
    String getType() {
        return Type;
    }
    boolean getAcoustic() {
        return Acoustic;
    }
    int getStringsID() {
        return StringsID;
    }
    String getInstallTimeStamp() {
        return InstallTimeStamp;
    }
    String getChangeTimeStamp() {
        return ChangeTimeStamp;
    }
    int getPlayTime() {
        return PlayTime;
    }
    int getSessionCnt() {
        return SessionCnt;
    }
    String getCurrSessionStart() {
        return CurrSessionStart;
    }
    int getLastSessionTime() {
        return LastSessionTime;
    }
    boolean getSessionInProgress() {
        return SessionInProgress;
    }

    // setters
    void setInstrID(int sid) {
        InstrID = sid;
    }
    void setBrand(String br) {
        Brand = br;
    }
    void setModel(String mod) {
        Model = mod;
    }
    void setType(String ty) {
        Type = ty;
    }
    void setAcoustic(boolean ac) {
        Acoustic = ac;
    }
    void setStringsID(int sid) {
        StringsID = sid;
    }
    void setInstallTimeStamp(String its) {
        InstallTimeStamp = its;
    }
    void setChangeTimeStamp(String cts) {
        ChangeTimeStamp = cts;
    }
    void setPlayTime(int pt) {
        PlayTime = pt;
    }
    void setSessionCnt(int scnt) {
        SessionCnt = scnt;
    }
    void setCurrSessionStart(String css) {
        CurrSessionStart = css;
    }
    void setLastSessionTime(int lst) {
        LastSessionTime = lst;
    }
    void setSessionInProgress(boolean ip) {
        SessionInProgress = ip;
    }

}

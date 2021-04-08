package com.example.stringtracker;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// StringTracker top level app state class with supporting functions   WKD 3-21-21
public class AppState extends Activity { //AppCompatActivity {
    private final String filename =  "STRunState.dat";  // fixed filename for entire app
    //path to data files on /sdcard (required) note that /system is a read-only file system
    // private final String path = "/USB STICK/Documents/StringTrackerData/";  // hardcoded path not good on moto g6
    //private final String path = "/Internal shared storage/Android/data/com.example.stringtracker_test/";  // hardcoded path not good on moto g6

    private final String path = Environment.getExternalStorageDirectory().getPath()+"/Documents/StringTrackerData/";
    private final String DELIM = "; ";  // delimiter for state passing data

    private static boolean FirstRun;       // flag to indicate initial run of app for setup purposes
    private static int InstrID;                // currently selected instrument
    private static boolean SessionStarted;     // set true at Start button event, false at Stop event
    private static boolean EnableSent;         // pref - enable user sent feedback
    private static int MaxSessionTime;         // max time for any given session
    private static int LastSessionTime;        // time for last session upon stop event
    private static int[] LifeThresholds = new int[4]; // thresholds for color coding
    private static int StringsCnt;             // to globally track number of strings and instruments added
    private static int InstrumentCnt;
    private static String CurrSessionStart;    // timestamp for start of current session

    private static long StartT, StopT;         // internal timestamps in mSec
    private static boolean testMode;           // true sets timescale to 0.01 sec instead of 1 min for session time
    private static String InstrState = null;
    private static String StringsState = null;

    // Constructors
    AppState(){      // defaults
        FirstRun = false;
        InstrID = 0;
        SessionStarted = false;
        EnableSent = false;
        MaxSessionTime = 180;  // 3 hour default
        LastSessionTime = 0;
        LifeThresholds[0] = 0;
        LifeThresholds[1] = 75;
        LifeThresholds[2] = 90;
        LifeThresholds[3] = 100;
        StringsCnt = 0;
        InstrumentCnt = 0;
        CurrSessionStart = null;
        StartT = 0;
        StopT = 0;
        testMode = false;
    }

    AppState( int insid, boolean sstart, boolean ensent, int maxsess, int lastsess,
                 int [] lifet, int scnt, int icnt, String currses){
        FirstRun = false;
        InstrID = insid;
        SessionStarted = sstart;
        EnableSent = ensent;
        MaxSessionTime = maxsess;  // 3 hour default
        LastSessionTime = lastsess;
        LifeThresholds = lifet;
        StringsCnt = scnt;
        InstrumentCnt = icnt;
        CurrSessionStart = currses;
        StartT = 0;
        StopT = 0;
        testMode = false;
    }


    // Method called by Start button click event
    public void startSession() {
        SessionStarted = true;
        CurrSessionStart = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        StartT = System.currentTimeMillis();
    }

    // Method called by Stop button click event or selecting another instrument
    public void stopSession() {
        SessionStarted = false;
        String CurrSessionStop = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        StopT = System.currentTimeMillis();
        if(testMode) {
            // 100ths of a sec time unit for testMode
            LastSessionTime = (int) TimeUnit.MILLISECONDS.toMillis(StopT - StartT) / 10;
        } else {
            // minutes played for normal operation
            LastSessionTime = (int) TimeUnit.MILLISECONDS.toMinutes(StopT - StartT) ;
        }

        if(LastSessionTime > MaxSessionTime) {   // truncate time if too long
            LastSessionTime = MaxSessionTime;
        }
    }


    // Method returns a string of AppState data using comma delimiters
    public String getAppState(){
        String outstr =
                String.valueOf(InstrID)+DELIM+
                        String.valueOf(SessionStarted)+DELIM+
                        String.valueOf(EnableSent)+DELIM+
                        String.valueOf(MaxSessionTime)+DELIM+
                        String.valueOf(LastSessionTime)+DELIM+
                        String.valueOf(LifeThresholds[0])+DELIM+
                        String.valueOf(LifeThresholds[1])+DELIM+
                        String.valueOf(LifeThresholds[2])+DELIM+
                        String.valueOf(LifeThresholds[3])+DELIM+
                        String.valueOf(StringsCnt)+DELIM+
                        String.valueOf(InstrumentCnt)+DELIM+
                        CurrSessionStart+DELIM+
                        String.valueOf(StartT)+DELIM+
                        String.valueOf(StopT)+DELIM+
                        String.valueOf(testMode);
        return outstr;
    }


    // Method to set App State parameters from a string using comma delimiters
    public void setAppState(String line){
        if (line != null) {
            String tokens[] = line.split(DELIM.trim());

            InstrID = Integer.parseInt(tokens[0].trim());
            SessionStarted = Boolean.parseBoolean(tokens[1].trim());
            EnableSent = Boolean.parseBoolean(tokens[2].trim());
            MaxSessionTime = Integer.parseInt(tokens[3].trim());
            LastSessionTime = Integer.parseInt(tokens[4].trim());
            LifeThresholds[0] = Integer.parseInt(tokens[5].trim());
            LifeThresholds[1] = Integer.parseInt(tokens[6].trim());
            LifeThresholds[2] = Integer.parseInt(tokens[7].trim());
            LifeThresholds[3] = Integer.parseInt(tokens[8].trim());
            StringsCnt = Integer.parseInt(tokens[9].trim());
            InstrumentCnt = Integer.parseInt(tokens[10].trim());
            CurrSessionStart = tokens[11];
            StartT = Long.parseLong(tokens[12].trim());
            StopT = Long.parseLong(tokens[13].trim());
            testMode = Boolean.parseBoolean(tokens[14].trim());

        }
    }

    // Method to init() the app which should run in main activity onCreate().
    // Checks for run state file existence then runs loadRunState() if run state file exists.
    // Otherwise it sets the FirstRun flag and returns value to main activity.
    // This implies default values are used.
    public boolean init() {
        File filechk = new File(path+filename);
        if(filechk.exists()) {
            loadRunState();  // if file exists load the data
            FirstRun = false;
            StartT = 0;
            StopT = 0;
        } else {
            File f = new File(path);
            f.mkdirs();   // create missing directories path
            saveRunState();  // add the first AppState file
            FirstRun = true;    // we may want to add other criterion here
        }
        return FirstRun;
    }

    // method returns true if app state file exists DEBUG purposes?
    boolean fileExists() {
        File filechk = new File( path+filename);
        return filechk.exists();
    }

    // Method to load app run state from app state file
    public void loadRunState() {
        String line = null;

        File data = new File(path+filename);
        // reads in one line of text then parses data
        try {
            InputStream f = new FileInputStream(data);
            BufferedReader br = new BufferedReader(new InputStreamReader(f));
            line = br.readLine();
            InstrState = br.readLine();
            StringsState = br.readLine();

            br.close();
        } catch (IOException e) {
             e.printStackTrace();
        }

        //parse if valid data
        if (line != null) {
            String tokens[] = line.split(",");

            InstrID = Integer.parseInt(tokens[0].trim());
            SessionStarted = Boolean.parseBoolean(tokens[1].trim());
            EnableSent = Boolean.parseBoolean(tokens[2].trim());
            MaxSessionTime = Integer.parseInt(tokens[3].trim());
            LastSessionTime = Integer.parseInt(tokens[4].trim());
            LifeThresholds[0] = Integer.parseInt(tokens[5].trim());
            LifeThresholds[1] = Integer.parseInt(tokens[6].trim());
            LifeThresholds[2] = Integer.parseInt(tokens[7].trim());
            LifeThresholds[3] = Integer.parseInt(tokens[8].trim());
            StringsCnt = Integer.parseInt(tokens[9].trim());
            InstrumentCnt = Integer.parseInt(tokens[10].trim());
            CurrSessionStart = tokens[11];
            StartT = Long.parseLong(tokens[12].trim());
            StopT = Long.parseLong(tokens[13].trim());
        }
    }

    // Method to save app run state to file (overwrites)
    public void saveRunState() {
        String delim = ", ";

        try {
            // note /system is a read-only file system so data is stored in /sdcard
            FileWriter filewriter = new FileWriter(path+filename, false);	// false = overwrite file
            PrintWriter outfile = new PrintWriter(filewriter);

            String outstr =
                    String.valueOf(InstrID)+delim+
                            String.valueOf(SessionStarted)+delim+
                            String.valueOf(EnableSent)+delim+
                            String.valueOf(MaxSessionTime)+delim+
                            String.valueOf(LastSessionTime)+delim+
                            String.valueOf(LifeThresholds[0])+delim+
                            String.valueOf(LifeThresholds[1])+delim+
                            String.valueOf(LifeThresholds[2])+delim+
                            String.valueOf(LifeThresholds[3])+delim+
                            String.valueOf(StringsCnt)+delim+
                            String.valueOf(InstrumentCnt)+delim+
                            CurrSessionStart+delim+
                            String.valueOf(StartT)+delim+
                            String.valueOf(StopT);

            outfile.println(outstr);
            outfile.println(InstrState);
            outfile.println(StringsState);

             outfile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // getters
    // Method returns the state value for initialization sequence
    public boolean firstRun() {
        return FirstRun;
    }
    int getInstrID() {
        return InstrID;
    }
    String getFilename() {
        return filename;     // read only
    }
    boolean sessionStarted() {  // shorter name for ease of use
        return SessionStarted;
    }
    boolean getEnableSent() {
        return EnableSent;
    }
    int getMaxSessionTime() {
        return MaxSessionTime;
    }
    int getLastSessionTime() {
        return LastSessionTime;
    }
    int [] getLifeThresholds() {
        return LifeThresholds;
    }
    int getStringsCnt() {
        return StringsCnt;
    }
    int getInstrumentCnt() {
        return InstrumentCnt;
    }
    String getCurrSessionStart() {
        return CurrSessionStart;
    }
    long getStartT(){ return StartT; }
    long getStopT(){ return StopT; }
    boolean getTestMode(){ return testMode; }
    // for returning stored data object states
    String getInstState(){
        return InstrState;
    }
    String getStrState(){
        return StringsState;
    }

    // setters
    void setFirstRun(boolean f) {  // for debug purposes
        FirstRun = f;
    }
    void setInstrID(int iid) {
        InstrID = iid;
        System.out.println("*** InstrumentID = "+InstrID);
    }
    void setSessionStarted(boolean ss) {
        SessionStarted = ss;
    }
    void setEnableSent(boolean es) {
        EnableSent = es;
    }
    void setMaxSessionTime(int mt) {
        MaxSessionTime = mt;
    }
    void setLastSessionTime(int st){ LastSessionTime = st; }
    void setLifeThresholds(int [] lt) {
        LifeThresholds = lt;
    }
    void setStringsCnt(int sc) {
        StringsCnt = sc;
    }
    void setInstrumentCnt(int ic) {
        InstrumentCnt =ic;
    }
    void setCurrSessionStart(String cs) {
        CurrSessionStart = cs;
    }
    void setTestMode(boolean b) { testMode = b; }
    void setInstState(String is){InstrState = is; }
    void setStrState(String ss){StringsState = ss; }
}

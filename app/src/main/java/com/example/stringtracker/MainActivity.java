package com.example.stringtracker;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

// StringTracker main activity class WKD
public class MainActivity extends AppCompatActivity implements SessionSentiment.SaveSentListener {
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();

    //  This flag causes the program to generate 10 random instruments and string sets
    //  then insert them into the DBs upon initial run after installation.
    //  Set this to false for normal build
    public static final boolean TEST_INIT_DB = true;

    public static final int TEXT_REQUEST = 1;
    static final String STATE_FRAGMENT = "state_of_fragment";
    Button buttonStartSes;
    Button buttonSelInst;
    private String selInstrText;
    private String selStrText;

    // main data objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    SessionSent Sent1 = new SessionSent();

    private TextView selInstTV;
    private TextView selStrTV;
    private TextView timeDebugTV;
    private TextView stringsLifeTV;
    Context context = MainActivity.this;  // activity context needed for DB calls

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize AppState.
        selInstTV = (TextView) findViewById(R.id.selInstrTV);
        selStrTV = (TextView) findViewById(R.id.selStringsTV);
        stringsLifeTV = (TextView) findViewById(R.id.stringsLifeTV);
        timeDebugTV = (TextView) findViewById(R.id.timeDebug);
        timeDebugTV.setBackgroundResource(R.color.background1);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int colorCodeDark = Color.parseColor("#297D6F");
        window.setStatusBarColor(colorCodeDark);

        // Check for first run of program and init to defaults otherwise load previous state
        if (A1.init()){    // if A1.init() returns true it is the first time the app has been run

            if(TEST_INIT_DB) {
                populateDB();   // DEBUG populate DB with 10 random instruments and string sets
            }

            A1.setInstState(I1.getInstState());  // update object state strings in AppState for 1st run
            A1.setStrState(S1.getStrState());
            updateSelDisplay("FirstRun: ");

        } else {
            loadAppState();
            updateSelDisplay(null);

            // restore Instrument and Strings fro DB
            I1.loadInstr(A1.getInstrID(), context);
            S1.loadStrings(I1.getStringsID(), context);
        }

        // Start/Stop Session Button
        buttonStartSes = findViewById(R.id.startButton);
        // Set startup button color based on run state
        if(!A1.sessionStarted()) {
            buttonStartSes.setText("Start");
            buttonStartSes.setBackgroundColor(0xff00A020);
        } else {
            buttonStartSes.setText("Stop");
            buttonStartSes.setBackgroundColor(0xffb02020);
        }

        buttonStartSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(A1.sessionStarted()) {  // Stop session
                    stopSession();
                } else {                // Start session
                    A1.startSession();
                    I1.setCurrSessionStart(A1.getCurrSessionStart());
                    buttonStartSes.setText("Stop");
                    buttonStartSes.setBackgroundColor(0xffb02020);

                    timeDebugTV.setText("Session Started");
                    timeDebugTV.setVisibility(View.VISIBLE);
                }
                saveAppState();
            }
        });

        // Select Instrument Button
        buttonSelInst = findViewById(R.id.selInstrButton);
        buttonSelInst.setBackgroundColor(Color.parseColor("#518078"));
        buttonSelInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a session is in progress go through the stop session sequence
                if(A1.sessionStarted()) {  // Stop session
                    stopSession();
                    // update DB items and save app run state
                    I1.updateInstr(I1.getInstrID(), context);
                    S1.updateStrings(S1.getStringsID(), context);
                } else {
                    // lock button if first run of the program => no data in DB yet
                    if (!A1.firstRun()) {
                        launchSelectInstrument(v);
                    }
                    saveAppState();
                }
            }
        });
    }  // end OnCreate //////////////

    // Method that goes through the StopSession sequences prompting th euser for Sentiment Feedback if enabled
    void stopSession(){
        A1.stopSession();
        I1.setSessionCnt(I1.getSessionCnt() + 1); // inc session cnt
        I1.setLastSessionTime(A1.getLastSessionTime());
        I1.setPlayTime(I1.getPlayTime() + I1.getLastSessionTime());
        buttonStartSes.setText("Start");
        buttonStartSes.setBackgroundColor(0xff00A020);
        // update DB items
        I1.updateInstr(I1.getInstrID(), context);
        S1.updateStrings(S1.getStringsID(), context);

        if (A1.getEnableSent()) {
            SessionSentiment SentDialog = new SessionSentiment();
            FragmentManager fmanager = getSupportFragmentManager();
            SentDialog.show(fmanager, "RateStrings");

        }
        updateSelDisplay(null);
        System.out.println("App:"+A1.getAppState());
        System.out.println("Ins:"+I1.getInstState());
        System.out.println("Str"+S1.getStrState());
    }

    // helper to restore app and data object states
    public void loadAppState() {
        A1.loadRunState();  // load prev app state and restore data objects
        I1.setInstState(A1.getInstState());
        S1.setStrState(A1.getStrState());
    }

    // helper to store app and data object states
    public void saveAppState() {
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();
    }

    // Methods to launch activities
    public void launchConfig(View view) {
        Log.d(LOG_TAG, "Config Button clicked!");
        saveAppState();
        Intent intent = new Intent(this, Configuration.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);   // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    public void launchConfig2(View view) {
        Log.d(LOG_TAG, "Config2 Button clicked!");
        saveAppState();
        Intent intent = new Intent(this, Configuration2.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);   // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    public void launchAnalytics(View view) {
        Log.d(LOG_TAG, "Analytics Button clicked!");
        saveAppState();
        Intent intent = new Intent(this, Analytics.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);    // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);

        // button lockout for first rum
        if (!A1.firstRun()) {
            startActivityForResult(intent, TEXT_REQUEST);
        }
    }

    public void launchSelectInstrument(View view) {
        Log.d(LOG_TAG, "Sel Instrument Button clicked!");
        saveAppState();
        Intent intent = new Intent(this, SelectInstrument.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);    // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);

        // button lockout for first rum
        if (!A1.firstRun()) {
            startActivityForResult(intent, TEXT_REQUEST);
        }
    }

    // Method collects returned appState strings (or other messages) and updates object
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String appState;
        String instState;
        String strState;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                appState = data.getStringExtra("appstate");
                instState = data.getStringExtra("inststate");
                strState = data.getStringExtra("strstate");
                //A1.loadRunState();  // DEBUG tests using file for message passing
                A1.setAppState(appState);  // Restore data object states on return
                I1.setInstState(instState);
                S1.setStrState(strState);

                System.out.println("*** RETURN to MAIN InstrID = "+I1.getInstrID());
                updateSelDisplay(null);
                saveAppState();
            }
            // DEBUG MESSAGES
            if (resultCode == RESULT_CANCELED) {
                selInstTV.setText("CANCELED!");
                selInstTV.setVisibility(View.VISIBLE);

            }
        }
    }

    // Method called by SessionSentiment dialog to report back ratings
    @Override
    public void didFinishSentDialog(float ratingProj, float ratingTone, float ratingInton) {
        Sent1.setProj(ratingProj);
        Sent1.setTone(ratingTone);
        Sent1.setInton(ratingInton);
        Sent1.setSessTime(I1.getLastSessionTime());
        Sent1.setTimeStamp(I1.getCurrSessionStart());
        I1.logSessionSent(Sent1);

        Toast.makeText(MainActivity.this, "Ratings:  "
                + String.format("Proj=%.2f Tone=%.2f Inton=%.2f", ratingProj, ratingTone, ratingInton), Toast.LENGTH_LONG).show();
    }

    // Helper to update instrument and strings selected
    public void updateSelDisplay(String prefix){
        if(prefix != null) {
            selInstrText = prefix + "Instrument ID:" + A1.getInstrID() + " - " + I1.getBrand() + ": " + I1.getModel();
        } else {
            selInstrText = "Instrument ID:" + A1.getInstrID() + " - " + I1.getBrand() + ": " + I1.getModel();
        }
        selInstTV.setText(selInstrText);
        selInstTV.setBackgroundResource(R.color.background1);
        selInstTV.setVisibility(View.VISIBLE);
        selStrText = "Strings ID:" + S1.getStringsID() + " - " + S1.getBrand() + ": " + S1.getModel();

        // select background color based on strings AvgLife
        if(S1.getAvgLife()>0) {
            int pctlife = (int) (((float) I1.getPlayTime() / (float) S1.getAvgLife()) * 100.0f);
            if (pctlife < A1.getLifeThresholds()[1]) {
                stringsLifeTV.setBackgroundResource(R.color.lifecolor1);
            } else if (pctlife < A1.getLifeThresholds()[2]) {
                stringsLifeTV.setBackgroundResource(R.color.lifecolor2);
            } else if (pctlife < A1.getLifeThresholds()[3]) {
                stringsLifeTV.setBackgroundResource(R.color.lifecolor3);
            } else {
                stringsLifeTV.setBackgroundResource(R.color.lifecolor4);
            }
            stringsLifeTV.setText("     Remaining Life: "+Integer.toString(100-pctlife)+"%     ");
        } else{
            stringsLifeTV.setBackgroundResource(R.color.lifecolor0);   // color for 1st stringset
        }
        stringsLifeTV.setVisibility(View.VISIBLE);
        selStrTV.setText(selStrText);
        selStrTV.setVisibility(View.VISIBLE);
        timeDebugTV.setBackgroundResource(R.color.background1);
        String timeText = "SessionCnt = "+I1.getSessionCnt() + "  LastSessTime = "
                + I1.getLastSessionTime() + "min \nTotalPlayT = " + I1.getPlayTime()+"min";

        timeDebugTV.setText(timeText);
        timeDebugTV.setVisibility(View.VISIBLE);

    }

    // DEBUG routine to insert 10 random instruments and strings into the DB
    void populateDB(){
        for(int j = 0; j<10; ++j){
            genStrInst();
            S1.insertStrings(context);
            I1.insertInstr(context);
            A1.setInstrumentCnt(A1.getInstrumentCnt()+1);
            A1.setStringsCnt(A1.getStringsCnt()+1);
            A1.setFirstRun(false);  // since we have now populated the DB we set firstRun to false;
        }
    }
    /////////////////////////////////////////////////////////////////////////
    // DEBUG test code randomly generates instrument and stringset values for A1 and S1
    public void genStrInst() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand[] = {"GHS", "D'Addario", "Martin", "Elixir", "Thomastik"};
        String sModel[] = {"A-270", "J-74", "Bright-Bronze", "Stainless-FW", "Precision"};
        String sTension[] = {"X-Light", "Light", "Medium", "Heavy"};
        String sType[] = {"Guitar", "Banjo", "Mandolin", "Violin", "Cello"};

        String iBrand[] = {"Gibson", "Collings", "Fender", "Taylor", "Weber"};
        String iModel[] = {"L5", "D-28", "Artist", "F-5", "Yellowstone"};

        int rand_sBr = rand.nextInt(5);
        int rand_sMo = rand.nextInt(5);
        int rand_sTe = rand.nextInt(4);
        int rand_sTy = rand.nextInt(5);
        int rand_sID = rand.nextInt(4)+1;

        int rand_iBr = rand.nextInt(5);
        int rand_iMo = rand.nextInt(5);
        int rand_iID = rand.nextInt(4)+1;

        int rand_cost = rand.nextInt(40)+5;
        int rand_changeCnt = rand.nextInt(10)+1;
        int rand_avgLife = rand.nextInt(600)+480;

        float [] proj = new float[10];
        float [] tone = new float[10] ;
        float [] inton = new float[10];
        float stval = 3.0f;
        float stdif = 0.20f;
        proj[0] = (float)rand.nextInt(100)/80.0f + 3.4f;
        tone[0] = (float)rand.nextInt(100)/80.0f + 3.0f;
        inton[0] = (float)rand.nextInt(100)/80.0f + 3.2f;
        for(int i = 1; i<10; ++i){
            if(proj[i-1]<1.6f){
                proj[i] = proj[i-1] + stdif + (float)i*(float)rand.nextInt(100)/500.0f;
            } else if(proj[i-1]>4.3f){
                proj[i] = proj[i-1] - stdif - (float)i*(float)rand.nextInt(100)/580.0f;
            } else {
                proj[i] = proj[i-1] + stdif - (float)i*(float)rand.nextInt(100)/540.0f;
            }

            if(tone[i-1]<1.7f){
                tone[i] = tone[i-1] + stdif + (float)i*(float)rand.nextInt(100)/450.0f;
            } else if(tone[i-1]>4.1f){
                tone[i] = tone[i-1] - stdif - (float)i*(float)rand.nextInt(100)/400.0f;
            } else {
                tone[i] = tone[i-1] + stdif - (float)i*(float)rand.nextInt(100)/480.0f;
            }

            if(inton[i-1]<1.8f){
                inton[i] = inton[i-1] + stdif + (float)i*(float)rand.nextInt(100)/600.0f;
            } else if(inton[i-1]>4.5f){
                inton[i] = inton[i-1] - stdif - (float)i*(float)rand.nextInt(100)/650.0f;
            } else {
                inton[i] = inton[i-1] + stdif - (float)i*(float)rand.nextInt(100)/680.0f;
            }
        }
        S1.setAvgProj(proj);
        S1.setAvgTone(tone);
        S1.setAvgInton(inton);
        S1.convFloat2Str();

        S1.setStringsID(rand_sID);
        S1.setBrand(sBrand[rand_sBr]);

        S1.setModel(sModel[rand_sMo]);
        S1.setTension(sTension[rand_sTe]);
        S1.setType(sType[rand_sTy]);
        S1.setAvgLife(rand_avgLife);
        S1.setChangeCnt(rand_changeCnt);
        S1.setCost(rand_cost);

        I1.setInstrID(rand_iID);
        I1.setBrand(iBrand[rand_iBr]);
        I1.setModel(iModel[rand_iMo]);
        I1.setType(sType[rand_sTy]);
        I1.setPlayTime(0);
        I1.setSessionCnt(0);
        I1.setStringsID(rand_sID);  // match up strings with instrument
        I1.setChangeTimeStamp(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

    }
/////////////////////////////////////////////////////////////////////////
}
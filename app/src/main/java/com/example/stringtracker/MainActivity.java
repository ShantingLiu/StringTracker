package com.example.stringtracker;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();

    public static final String EXTRA_MESSAGE
            = "com.example.android.StringTracker_test.extra.MESSAGE";

    public static final int TEXT_REQUEST = 1;
    private TextView selectedInstrTV;
    private TextView mStringsSel;
    private Button mButton;
    private boolean isFragmentDisplayed = false;
    static final String STATE_FRAGMENT = "state_of_fragment";
    Button buttonStartSes;
    Button buttonSelInst;

    // main data objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    SessionSent Sent1 = new SessionSent();

    private TextView selInstTV;
    private TextView selStrTV;
    private TextView timeDebugTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize AppState.
        String appstate = null;
        selInstTV = (TextView) findViewById(R.id.selInstrTV);
        selStrTV = (TextView) findViewById(R.id.selStringsTV);
        timeDebugTV = (TextView) findViewById(R.id.timeDebug);


        if (A1.init()) {      // if .init() returns true it is the first time the app has been run
            // TODO - direct user to Configuration to add StingSet and Instrument
            //genStrInst();  // DEBUG test stuff
            appstate = A1.getAppState();

            A1.setInstState(I1.getInstState());  // update object state strings in AppState for 1st run
            A1.setStrState(S1.getStrState());

            selInstTV.setText("FirstRun: " + A1.getInstrID() + " Instrument:" + I1.getInstrID() + " " + I1.getBrand() + " " + I1.getModel());
            selInstTV.setVisibility(View.VISIBLE);
            selStrTV.setText(S1.getStringsID() + " Strings: " + S1.getStringsID() + " " + S1.getBrand() + " " + S1.getModel() + " " + S1.getAvgProjStr());
            selStrTV.setVisibility(View.VISIBLE);

        } else {
            loadAppState();

            selInstTV.setText(A1.getInstrID() + " Instrument:" + I1.getInstrID() + " " + I1.getBrand() + " " + I1.getModel());
            selInstTV.setVisibility(View.VISIBLE);
            selStrTV.setText(S1.getStringsID() + " Strings: " + S1.getStringsID() + " " + S1.getBrand() + " " + S1.getModel() + " " + S1.getAvgProjStr());
            selStrTV.setVisibility(View.VISIBLE);


            // TODO - populate S1 and I1 from DB
            // DB.getInstrument(I1, A1.getInstrID());
            // DB.getStringSet(S1, I1.getStringsID());
        }
        A1.setTestMode(true);
        A1.setEnableSent(true);
        A1.setMaxSessionTime(200);

        buttonStartSes = findViewById(R.id.startButton);
        buttonStartSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (A1.sessionStarted()) {  // Stop session
                    A1.stopSession();
                    I1.setSessionCnt(I1.getSessionCnt() + 1); // inc session cnt
                    I1.setLastSessionTime(A1.getLastSessionTime());
                    I1.setPlayTime(I1.getPlayTime() + I1.getLastSessionTime());
                    buttonStartSes.setText("Start");
                    buttonStartSes.setBackgroundColor(GREEN);
                    if (A1.getEnableSent()) {
                        // TODO call user sent dialog
                        ////////////////////////
                        if (A1.getTestMode()) {
                            I1.logSessionSent(genRandSent());  // DEBUG store random sent to log file normally from sent dialog
                        }
                    }

                    timeDebugTV.setText(I1.getSessionCnt() + " Elapsed t = " + (A1.getStopT() - A1.getStartT()) + " LastSessT = " + I1.getLastSessionTime() + " PlayT = " + I1.getPlayTime());
                    timeDebugTV.setVisibility(View.VISIBLE);

                } else {                // Start session
                    A1.startSession();
                    buttonStartSes.setText("Stop");
                    buttonStartSes.setBackgroundColor(RED);

                    timeDebugTV.setText("StartT = " + A1.getStartT());
                    timeDebugTV.setVisibility(View.VISIBLE);
                }
                saveAppState();
            }
        });

        buttonSelInst = findViewById(R.id.selInstrButton);
        buttonSelInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// DEBUG simulates string change ////
                I1.logStringChange();
                // do not attempt to update if there are no sessions
                if (I1.getPlayTime() > 0) {

                    S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());

                    if (!A1.getTestMode()) {  // if in normal operating mode clear sent log
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // TODO this is where DBs are updated at string change
                I1.init();
                //////////////////////
                genStrInst();  // DEBUG test stuff generate a random instr strings combo
                A1.setInstrID(I1.getInstrID());
                A1.setInstrumentCnt(1);
                A1.setStringsCnt(1);

                saveAppState();

                selInstTV.setText(A1.getInstrID() + " Instrument:" + I1.getInstrID() + " " + I1.getBrand() + " " + I1.getModel());
                selInstTV.setVisibility(View.VISIBLE);
                selStrTV.setText(S1.getStringsID() + " Strings: " + S1.getStringsID() + " " + S1.getBrand() + " " + S1.getModel() + " " + S1.getAvgProjStr());
                selStrTV.setVisibility(View.VISIBLE);

            }
        });

    }  // end OnCreate

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
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();

        Intent intent = new Intent(this, Configuration.class);
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
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();

        Intent intent = new Intent(this, Analytics.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);    // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);

        startActivityForResult(intent, TEXT_REQUEST);

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

                selInstTV.setText(A1.getInstrID() + " Instrument:" + I1.getInstrID() + " " + I1.getBrand() + " " + I1.getModel());
                selInstTV.setVisibility(View.VISIBLE);
                selStrTV.setText(S1.getStringsID() + " Strings: " + S1.getStringsID() + " " + S1.getBrand() + " " + S1.getModel() + " " + S1.getAvgProjStr());
                selStrTV.setVisibility(View.VISIBLE);
            }
            // DEBUG MESSAGES
            if (resultCode == RESULT_CANCELED) {
                selInstTV.setText("CANCELED!");
                selInstTV.setVisibility(View.VISIBLE);

            }
        }
    }


/////////////////////////////////////////////////////////////////////////

    // DEBUG test code randomly generates instrument and stringset values for A1 and S1
    public void genStrInst() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand[] = {"GHS", "D'Addario", "Martin", "Elixir", "Ernie Bal"};
        String sModel[] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};
        String sTension[] = {"XL", "Light", "Medium", "Heavy"};
        String sType[] = {"guitar", "banjo", "mandolin", "violin", "cello"};

        String iBrand[] = {"Gibson", "Collings", "Fender", "Taylor", "PRS"};
        String iModel[] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};

        int rand_sBr = rand.nextInt(5);
        int rand_sMo = rand.nextInt(5);
        int rand_sTe = rand.nextInt(4);
        int rand_sTy = rand.nextInt(5);
        int rand_sID = rand.nextInt(20) + 10;

        int rand_iBr = rand.nextInt(5);
        int rand_iMo = rand.nextInt(5);
        int rand_iID = rand.nextInt(20) + 100;

        S1.setStringsID(rand_sID);
        S1.setBrand(sBrand[rand_sBr]);

        S1.setModel(sModel[rand_sMo]);
        S1.setTension(sTension[rand_sTe]);
        S1.setType(sType[rand_sTy]);

        I1.setInstrID(rand_iID);
        I1.setBrand(iBrand[rand_iBr]);
        I1.setModel(iModel[rand_iMo]);
        I1.setType(sType[rand_sTy]);
        I1.setStringsID(rand_sID);  // match up strings with instrument

    }

    public SessionSent genRandSent() {
        SessionSent S = new SessionSent();
        Random rand = new Random();
        int rand_sent = rand.nextInt(10);
        S.setProj((float) rand_sent * 0.5f);
        rand_sent = rand.nextInt(10);
        S.setTone((float) rand_sent * 0.5f);
        rand_sent = rand.nextInt(10);
        S.setInton((float) rand_sent * 0.5f);
        S.setSessTime((int) A1.getLastSessionTime());
        S.setTimeStamp(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

        return S;
    }

//////////////////////////////////////////////////////////

/*  // TODO make this work!
    public void displayFragment() {
        selectInstr selinstr = selectInstr.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager(); // use fragment manager
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        // Add the SimpleFragment.
        fragmentTransaction.add(R.id.fragment_container  ,
               selinstr).addToBackStack(null).commit();
        // Update the Button text.
        mButton.setText(R.string.close);
        // Set boolean flag to indicate fragment is open.
        isFragmentDisplayed = true;

    }

    public void closeFragment() {
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        SimpleFragment simpleFragment = (SimpleFragment) fragmentManager
                .findFragmentById(R.id.fragment_container);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        // Update the Button text.
        mButton.setText(R.string.open);
        // Set boolean flag to indicate fragment is closed.
        isFragmentDisplayed = false;
    }
*/

}
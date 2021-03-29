package com.example.stringtracker_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

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

        if(A1.init()){      // if .init() returns true it is the first time the app has been run
            // TODO - direct user to Configuration to add StingSet and Instrument
            //genStrInst();  // DEBUG test stuff
            appstate = A1.getAppState();

            selInstTV.setText("FirstRun:" +A1.getAppState() );
            selInstTV.setVisibility(View.VISIBLE);
            selStrTV.setText("Strings - " +I1.getStringsID() );
            selStrTV.setVisibility(View.VISIBLE);

        } else {
            A1.loadRunState();  // load prev app state

            selInstTV.setText("SubsequentRun:" +A1.getAppState() );
            selInstTV.setVisibility(View.VISIBLE);
            selStrTV.setText("Strings - " +I1.getStringsID() );
            selStrTV.setVisibility(View.VISIBLE);


            // TODO - populate S1 and I1 from DB
            // DB.getInstrument(I1, A1.getInstrID());
            // DB.getStringSet(S1, I1.getStringsID());
        }
        A1.setTestMode(true);
        A1.setEnableSent(true);
        A1.setMaxSessionTime(200);

       // selInstTV.setText("Instrument - " +A1.getInstrumentID() );

        buttonStartSes = findViewById(R.id.startButton);
        buttonStartSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(A1.sessionStarted()){  // Stop session
                    A1.stopSession();
                    I1.setSessionCnt(I1.getSessionCnt()+1); // inc session cnt
                    // update times limited to max value for session
                    //if((int)(A1.getStopT()-A1.getStartT())<A1.getMaxSessionTime()) {
                    I1.setLastSessionTime(A1.getLastSessionTime());
                    //} else{
                    //    I1.setLastSessionTime(A1.getMaxSessionTime());
                    //}
                    I1.setPlayTime(I1.getPlayTime()+I1.getLastSessionTime());
                    A1.saveRunState();

                    if(A1.getEnableSent()){
                        // TODO call user sent dialog
                        ////////////////////////
                        I1.logSessionSent(genRandSent());  // DEBUG store random sent to log file normally from sent dialog
                    }

                    timeDebugTV.setText(I1.getSessionCnt()+" Elapsed t = "+(A1.getStopT()-A1.getStartT())+" LastSessT = "+ I1.getLastSessionTime()+" PlayT = "+I1.getPlayTime());
                    timeDebugTV.setVisibility(View.VISIBLE);


                } else {                // Start session
                    A1.startSession();
                    timeDebugTV.setText("StartT = " + A1.getStartT() );
                    timeDebugTV.setVisibility(View.VISIBLE);
                    A1.saveRunState();
                }
           }
        });

        buttonSelInst = findViewById(R.id.selInstrButton);
        buttonSelInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// DEBUG simulates string change ////
                I1.logStringChange();
                // do not attempt to update if there are no sessions
                if(I1.getSessionCnt()>0) {
                    S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());
                }

                // TODO this is where DBs are updated at string change
                I1.init();
                //////////////////////
                genStrInst();  // DEBUG test stuff generate a random instr strings combo
                A1.setInstrumentID(I1.getInstrID());
                I1.setStringsID(S1.getStringsID());
                A1.setInstrumentCnt(1);
                A1.setStringsCnt(1);
                A1.saveRunState();
                
                selInstTV.setText(A1.getInstrumentID()+" Instrument:"+I1.getInstrID() +" "+I1.getBrand()+" "+I1.getModel() );
                selInstTV.setVisibility(View.VISIBLE);
                selStrTV.setText(S1.getStringsID()+" Strings: "+S1.getStringsID()+" "+S1.getBrand()+" "+S1.getModel() );
                selStrTV.setVisibility(View.VISIBLE);

            }
        });

    }


    // Methods to launch activities
    public void launchConfig(View view){
        Log.d(LOG_TAG, "Config Button clicked!");
        A1.saveRunState();
        Intent intent = new Intent(this, Configuration.class);
        String appState = A1.getAppState();
        intent.putExtra("appstate", appState);
        startActivityForResult(intent, TEXT_REQUEST);
    }


    public void launchAnalytics(View view){
        Log.d(LOG_TAG, "Analytics Button clicked!");
        A1.saveRunState();
        Intent intent = new Intent(this, Analytics.class);
        String appState = A1.getAppState();
        intent.putExtra("appstate", appState);
        startActivityForResult(intent, TEXT_REQUEST);

    }


    // Method collects returned appState strings (or other messages) and updates object
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String appState;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                appState = data.getStringExtra("appstate");
                //A1.setAppState(appState);  // DEBUG tests message passed through intent to set appState
                A1.loadRunState();  // DEBUG tests using file for message passing
                // DEBUG messages
                selStrTV.setText("Returned InstrID = " +A1.getInstrumentID() );
                selStrTV.setVisibility(View.VISIBLE);
                selInstTV.setText("Updated InstrID:" +A1.getAppState() );
                selInstTV.setVisibility(View.VISIBLE);
                // A1.saveRunState();


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
    public void genStrInst(){
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand [] = {"GHS", "D'Addario", "Martin", "Elixir", "Ernie Bal"};
        String sModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};
        String sTension [] = {"XL", "Light", "Medium", "Heavy"};
        String sType [] = {"guitar", "banjo", "mandolin", "violin", "cello"};

        String iBrand [] = {"Gibson", "Collings", "Fender", "Taylor", "PRS"};
        String iModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};

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

    public SessionSent genRandSent(){
        SessionSent S = new SessionSent();
        Random rand = new Random();
        int rand_sent = rand.nextInt(10);
        S.setProj((float)rand_sent * 0.5f);
        rand_sent = rand.nextInt(10);
        S.setTone((float)rand_sent * 0.5f);
        rand_sent = rand.nextInt(10);
        S.setInton((float)rand_sent * 0.5f);
        S.setSessTime((int)A1.getLastSessionTime());
        S.setTimeStamp(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

        return S;
    }

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
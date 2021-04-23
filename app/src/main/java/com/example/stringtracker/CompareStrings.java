package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CompareStrings extends AppCompatActivity {

    // local copies of the main stringset, instrument, and appstate objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();   // 2nd stringset for comparison
    Instrument I1 = new Instrument();
    Button buttonRet;
    //testing
    Button Comparebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_strings);
        String appState;
        String instState;
        String strState;
        Intent mIntent = getIntent();      // passing stringset object states into compare activity
        appState = mIntent.getStringExtra("appstate");
        instState = mIntent.getStringExtra("inststate");
        strState = mIntent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);


        // Without return button the back arrow may go to a blank screen
        buttonRet = findViewById(R.id.buttonRet3);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //A1.setInstrumentID(888);
                //A1.saveRunState();  // DEBUG using stored data file for messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });



    } // OnCreate



}
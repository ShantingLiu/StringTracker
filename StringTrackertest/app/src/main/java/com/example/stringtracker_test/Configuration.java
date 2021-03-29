package com.example.stringtracker_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Configuration extends AppCompatActivity {
    // main data objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();

    private TextView configText;
    Button buttonRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        String appState;
        Intent mIntent = getIntent();
        appState = mIntent.getStringExtra("appstate");

        //A1.setAppState(appState);  // DEBUG this tests message passing in intent

        if (false){   //A1.init()) {      // if .init() returns true it is the first time the app has been run
            configText = (TextView) findViewById(R.id.configTextView);
            configText.setText("AppState file not found - " + A1.getInstrumentID());
            configText.setVisibility(View.VISIBLE);

            //A1.saveRunState();
            // TODO - direct user to Configuration to add StingSet and Instrument
        } else {
            A1.loadRunState();  // load prev app state
            configText = (TextView) findViewById(R.id.configTextView);
            configText.setText("Config Screen - " + A1.getInstrumentID());
            configText.setVisibility(View.VISIBLE);
            // TODO - populate S1 and I1 from DB
            // DB.getInstrument(I1, A1.getInstrID());
            // DB.getStringSet(S1, I1.getStringsID());
        }


        buttonRet = findViewById(R.id.buttonRet);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //A1.setInstrumentID(777);
                //A1.saveRunState();  // DEBUG test using stored file for messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }


        });
    }
}
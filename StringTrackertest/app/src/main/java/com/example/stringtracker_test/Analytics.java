package com.example.stringtracker_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Analytics extends AppCompatActivity {
    // main data objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Button buttonRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        String appState;
        Intent mIntent = getIntent();
        appState = mIntent.getStringExtra("appstate");

        //A1.setAppState(appState);
        A1.loadRunState();

        TextView analyticsTV;
        analyticsTV = (TextView) findViewById(R.id.analyticsTV);
        analyticsTV.setText("AppState InstrID - " +A1.getInstrumentID() );
        analyticsTV.setVisibility(View.VISIBLE);

        buttonRet = findViewById(R.id.buttonRet2);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //A1.setInstrumentID(888);
                //A1.saveRunState();  // DEBUG using stored data file for messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }


        });

    }
}
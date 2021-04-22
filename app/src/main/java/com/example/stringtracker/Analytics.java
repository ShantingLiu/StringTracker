package com.example.stringtracker;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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
    TextView analyticsTV;
    TextView instrLabelTV;
    TextView stringsLabelTV;
    TextView stringStatsTV1;
    TextView stringStatsTV2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        String appState;
        String instState;
        String strState;
        Intent mIntent = getIntent();
        appState = mIntent.getStringExtra("appstate");
        instState = mIntent.getStringExtra("inststate");
        strState = mIntent.getStringExtra("strstate");

        A1.setAppState(appState);  // init objects from intent
        I1.setInstState(instState);
        S1.setStrState(strState);

        analyticsTV = (TextView) findViewById(R.id.analyticsTV);
        instrLabelTV = (TextView) findViewById(R.id.instrLabelTV);
        stringsLabelTV = (TextView) findViewById(R.id.stringsLabelTV);
        stringStatsTV1 = (TextView) findViewById(R.id.strstatsTV1);
        stringStatsTV2 = (TextView) findViewById(R.id.strstatsTV2);
        updateStatsDisplay();

        buttonRet = findViewById(R.id.buttonRet2);
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

    }   // OnCreate

    // Method to update the strings statistics textview
    public void updateStatsDisplay(){
        int pctLife = 0;
        float costPerHr = 0.0f;
        float costPerHrExp = 0.0f;
        if(I1.getSessionCnt()>0){
            pctLife = 100 - (int)(100.0*(float)I1.getPlayTime()/(float)S1.getAvgLife());
            costPerHr = (S1.getCost()/(float)(I1.getPlayTime()/60));
            if(S1.getAvgLife()>0){
                costPerHrExp = (S1.getCost()/(float)(S1.getAvgLife()/60));
            }
        }
        String selInstr =  "Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel()+" ("+I1.getType()+")";
        String selStrings =  "String Set ID:"+S1.getStringsID()+" "+S1.getBrand()+"-"+S1.getModel()+" ("+S1.getType()+")";
        String strStats1 =  "Avg Life:"+S1.getAvgLife()+"min  Time played:"+I1.getPlayTime()+"min  Life remaining:"+pctLife+"%";
        String strStats2 =  "Cost/hr(current):"+String.format("%.2f", costPerHr)+" $/hr   Cost/hr(expected):"+String.format("%.2f",costPerHrExp)+" $/hr";
        analyticsTV.setText("Analytics for Current Selection");
        instrLabelTV.setText(selInstr);
        stringsLabelTV.setText(selStrings);
        stringStatsTV1.setText(strStats1);
        stringStatsTV2.setText(strStats2);

        analyticsTV.setVisibility(View.VISIBLE);
        instrLabelTV.setVisibility(View.VISIBLE);
        stringsLabelTV.setVisibility(View.VISIBLE);
        stringStatsTV1.setVisibility(View.VISIBLE);
        stringStatsTV2.setVisibility(View.VISIBLE);
    }

    public void gotoCompareStrings(View v){
        Intent intent = new Intent(this, CompareStrings.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);   // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        startActivity(intent);
    }

}
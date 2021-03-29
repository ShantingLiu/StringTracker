package com.example.stringtracker_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Configuration extends AppCompatActivity {
    // main data objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();

    private TextView configText;
    private EditText editInstBrand;
    private EditText editInstModel;

    Button buttonRet;
    Button buttonSave;
    EditText iBrand;
    EditText iModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

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

        iBrand = (EditText) findViewById(R.id.editTextBrand);
        iModel = (EditText) findViewById(R.id.editTextModel);
        iBrand.setText(I1.getBrand());  // load existing values in editText
        iModel.setText(I1.getModel());

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
         }


        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I1.setBrand(iBrand.getText().toString());
                I1.setModel(iModel.getText().toString());

                showToast(v);
            }

        });


    buttonRet = findViewById(R.id.buttonRet);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //A1.saveRunState();  // DEBUG test using stored file for messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

        });
    }


    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.toast_message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

}


package com.example.stringtracker;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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
    Button buttonLoad;
    EditText iBrand;
    EditText iModel;
    EditText iInstID;

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

        ////////// EXAMPLE DEBUG CODE - EditText and save button //////////
        iBrand = (EditText) findViewById(R.id.editTextBrand);
        iModel = (EditText) findViewById(R.id.editTextModel);
        iInstID = (EditText) findViewById(R.id.editTextInstrID);
        iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
        iModel.setText(I1.getModel());

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I1.setInstrID(Integer.parseInt(iInstID.getText().toString()));
                I1.setBrand(iBrand.getText().toString());
                I1.setModel(iModel.getText().toString());
                Context context = Configuration.this;
                if(I1.insertInstr(context)){
                    showToast(v);
                }
            }
        });


        ////// LOAD RECORD //////
        buttonLoad = findViewById(R.id.buttonLoad);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Configuration.this;
                if(I1.loadInstr(Integer.parseInt(iInstID.getText().toString()), context)){
                    showToast(v);
                }
                I1.setInstrID(Integer.parseInt(iInstID.getText().toString()));
                I1.setBrand(iBrand.getText().toString());
                I1.setModel(iModel.getText().toString());

             }
        });

        /////////////////////////////////////////////////////////////////

        // Return button - a good idea to keep this with state passing intact
        buttonRet = findViewById(R.id.buttonRet);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

        });
    } ///////////////// end of OnCreate

    // A handy toast Saved message you might want to use
    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.toast_message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

}

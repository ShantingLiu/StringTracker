
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddNewInstrument extends AppCompatActivity {
    private EditText newInstrBrandNamePrompt;
    private EditText newInstrModelNamePrompt;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = AddNewInstrument.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    private String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    private boolean acoustic = false;
    private Spinner spinnerInstrTypes;
    // TODO: Add a data structure to hold the Strings as well, possibly an ArrayList?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        newInstrBrandNamePrompt = findViewById(R.id.newInstrBrandName);
        newInstrModelNamePrompt = findViewById(R.id.newInstrModelName);
        Intent intent = getIntent();        //replyTo = intent.getStringExtra("fromActivity");
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        // initiate instrument type spinner
        addItemsOnInstrTypesSpinner();
        addListenerOnSpinnerItemSelection();
    }

    // add items into spinner
    public void addItemsOnInstrTypesSpinner(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrTypes.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        spinnerInstrTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // returns new instrument data back to activity it came from
    public void addNewInstr(View view){
        String instrBrandName = newInstrBrandNamePrompt.getText().toString();
        String instrModelName = newInstrModelNamePrompt.getText().toString();
        Intent resultIntent = new Intent();
        // TODO: Add instr info (brandName + modelName + instrType, etc) into an instr object and add into DB (look into config to see how this is done)
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

}
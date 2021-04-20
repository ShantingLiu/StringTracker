
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
    private String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    private boolean acoustic = false;
    private Spinner spinnerInstrTypes;
    private Spinner spinnerStrTension;
    CheckBox acousticCheckBox;
    // TODO: Add a data structure to hold the Strings as well, possibly an ArrayList?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        newInstrBrandNamePrompt = findViewById(R.id.newInstrBrandName);
        newInstrModelNamePrompt = findViewById(R.id.newInstrModelName);
        acousticCheckBox = findViewById(R.id.acousticCheckBox);
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
        // initiate str tension spinner
        addItemsOnStrTensionSpinner();
        addListenerOnStrTensionSpinnerItemSelection();
    }

    /////////////////SPINNERS START/////////////////////
    // add items into spinner for string tensions
    public void addItemsOnStrTensionSpinner(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner);
        ArrayAdapter<String> strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strTensions);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrTension.setAdapter(strDataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrTensionSpinnerItemSelection(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner);
        spinnerStrTension.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner for instrument types
    public void addItemsOnInstrTypesSpinner(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrTypes.setAdapter(dataAdapter);
    }

    // listener for instrument type spinner
    public void addListenerOnSpinnerItemSelection(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        spinnerInstrTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    /////////////////SPINNERS END/////////////////////

    // handles click event for acoustic checkbox
    public void onCheckBoxClicked(View view){
        // Is the view now checked?
        acoustic = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (acoustic){
            // TODO: set acoustic to true ((Am I doing this right?))
            I1.setAcoustic(true);
        } else {
            I1.setAcoustic(false);
        }
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
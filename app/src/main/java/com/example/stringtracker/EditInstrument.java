package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditInstrument extends AppCompatActivity {
    EditText iBrand;
    EditText iModel;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = EditInstrument.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    String instrTypePropercase;
    String instrTypeLowercase;
    private final String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    ArrayList<String> slist = new ArrayList<String>();
    private Spinner spinnerInstrTypes;
    private Spinner spinnerStr;
    CheckBox acousticCheckBox;
    Boolean isAcoustic;

    // *** Stops false select in spinners on initialization
    private boolean userIsInteracting = false;
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instrument);
        iBrand = (EditText) findViewById(R.id.editInstrBrandName);
        iModel = (EditText) findViewById(R.id.editInstrModelName);
        acousticCheckBox = findViewById(R.id.editInstrAcousticCheckbox);
        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);
        isAcoustic = I1.getAcoustic();
        System.out.println("isAcoustic init = " + isAcoustic);
        acousticCheckBox.setChecked(isAcoustic);
        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
        iModel.setText(I1.getModel());
        instrTypeLowercase = I1.getType();
        instrTypePropercase = instrTypeLowercase.substring(0, 1).toUpperCase() + instrTypeLowercase.substring(1);

        // populate list of strings
        try {
            slist = S1.getStringsStrList(context, I1.getType());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // initiate instrument type spinner
        addItemsOnInstrTypesSpinner();
        addListenerOnSpinnerItemSelection();
        // initiate instrument str selection
        addItemsStrSpinner();
        addListenerOnStrSpinnerItemSelection();

        // set onClicks for spinners
        // TODO: Don't allow this to run on default selection
        spinnerInstrTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // create a new adapter with the corresponding values
                if(userIsInteracting) {
                    instrTypeLowercase = spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString().toLowerCase();
                    instrTypePropercase = instrTypeLowercase.substring(0, 1).toUpperCase() + instrTypeLowercase.substring(1);
                    I1.setType(instrTypeLowercase);
                    S1.loadStrings(I1.getStringsID(), context); // maybe we don't need this line
                    slist.clear();
                    try {
                        slist = S1.getStringsStrList(context, I1.getType());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    addItemsStrSpinner();
                }
            } // I may have to put something similar to this code on the onActivityResult() from the addString screen

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing selected, so set empty options
            }
        });

        spinnerStr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp = slist.get(position);
                String token = tmp.split(":")[1];
                int newstringsid = Integer.parseInt(token.split(" ")[0].trim()); // TODO: Check with Keith to see if we need newstringsid here
                if(userIsInteracting) {
                    I1.logStringChange();
                    if (I1.getPlayTime() > 0 && I1.getSessionCnt() > 0) {
                        S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());
                        // removed testmode save of sent logs
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    System.out.println("*** STRING CHANGE *** new stringsID=" + I1.getStringsID());  // DEBUG

                    // set new instrumentID load new Instrument and StringSet from DB
                    I1.setStringsID(newstringsid);
                    S1.loadStrings(I1.getStringsID(), context);
                    I1.init();   // clear for new string cycle
                    I1.updateInstr(I1.getInstrID(), context);  // be sure to update DB item for new strings selected
                    A1.init();  // clear internal time values
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
            }
        });
    }

    /////////////////SPINNERS START/////////////////////
    // add items into spinner for instrument types
    public void addItemsOnInstrTypesSpinner(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.editInstrTypeSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrTypes.setAdapter(dataAdapter);
    }

    // listener for instrument type spinner
    public void addListenerOnSpinnerItemSelection(){ // TODO: DEBUG - This method is breaking the app
        spinnerInstrTypes = (Spinner) findViewById(R.id.editInstrTypeSpinner);
        spinnerInstrTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner for string selection
    public void addItemsStrSpinner(){
        spinnerStr = (Spinner) findViewById(R.id.editInstrStrSpinner);
        ArrayAdapter<String> dataAdapterStr = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slist);
        dataAdapterStr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStr.setAdapter(dataAdapterStr);
    }

    // listener for instrument string selection
    public void addListenerOnStrSpinnerItemSelection(){
        spinnerStr = (Spinner) findViewById(R.id.editInstrStrSpinner);
        spinnerStr.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    /////////////////SPINNERS END/////////////////////

    public void onCheckBoxClicked(View view) {
        isAcoustic = ((CheckBox) view).isChecked();
        I1.setAcoustic(isAcoustic);
    }

    // TODO: DEBUG - Updating InstrType not working
    // TODO: DEBUG - Updating isAcoustic not working  - Issue might be in Config
    public void updateInstr(View view){
        I1.setBrand(iBrand.getText().toString());
        I1.setModel(iModel.getText().toString());
        I1.setAcoustic(acousticCheckBox.isChecked());
        I1.updateInstr(I1.getInstrID(), context);  // Updates DB settings
        Intent resultIntent = new Intent();
        resultIntent.putExtra("replyInstruction", "NormalReturn");
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void deleteInstr(View view){
        Intent resultIntent = new Intent();
        A1.setInstrumentCnt(A1.getInstrumentCnt()-1);  // need
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        resultIntent.putExtra("replyInstruction", "000000000"); // bad coding practice, TODO fix later
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
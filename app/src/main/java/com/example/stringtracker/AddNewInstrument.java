
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
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
    ArrayList<String> slist = new ArrayList<String>();
    private boolean acoustic = false;
    private Spinner spinnerInstrTypes;
    private Spinner spinnerStr;
    CheckBox acousticCheckBox;
    ArrayAdapter<String> dataAdapterStr;
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
        spinnerInstrTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                // create a new adapter with the corresponding values
                I1.setType(spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString());
                S1.loadStrings(I1.getStringsID(), context); // maybe we don't need this line
                try {
                    slist = S1.getStringsStrList(context, I1.getType());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                addItemsStrSpinner();
            } // I may have to put something similar to this code on the onActivityResult() from the addString screen

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing selected, so set empty options
            }
        });

        spinnerStr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // TODO: Keith may want to double check this code block
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp = slist.get(position);
                String token = tmp.split(":")[1];
                int newstringsid = Integer.parseInt(token.split(" ")[0].trim());

                I1.logStringChange();
                if (I1.getPlayTime() > 0  && I1.getSessionCnt() > 0) {

                    S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());

                    if (!A1.getTestMode()) {  // if in normal operating mode clear sent log
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("*** STRING CHANGE *** new stringsID="+I1.getStringsID());  // DEBUG

                // set new instrumentID load new Instrument and StringSet from DB
                I1.setStringsID(newstringsid);
                S1.loadStrings(I1.getStringsID(), context);
                I1.init();   // clear for new string cycle
                I1.updateInstr(I1.getInstrID(), context);  // be sure to update DB item for new strings selected
                A1.init();  // clear internal time values
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
            }
        });

    }
    /////////////////SPINNERS START/////////////////////
    // add items into spinner for string tensions

    // add items into spinner for instrument types
    public void addItemsOnInstrTypesSpinner(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrTypes.setAdapter(dataAdapter);
    }

    // listener for instrument type spinner
    public void addListenerOnSpinnerItemSelection(){ // TODO: DEBUG - This method is breaking the app
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        spinnerInstrTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner for string selection
    public void addItemsStrSpinner(){
        spinnerStr = (Spinner) findViewById(R.id.strSpinner);
        dataAdapterStr = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slist);
        dataAdapterStr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStr.setAdapter(dataAdapterStr);
    }

    // listener for instrument string selection
    public void addListenerOnStrSpinnerItemSelection(){
        spinnerStr = (Spinner) findViewById(R.id.strSpinner);
        spinnerStr.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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
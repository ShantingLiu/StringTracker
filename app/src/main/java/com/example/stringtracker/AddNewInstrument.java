
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    final int ADD_NEW_STRING_REQUEST = 0;
    EditText iBrand;
    EditText iModel;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = AddNewInstrument.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    String instrTypePropercase;
    String instrTypeLowercase;
    private final String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    private final String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    ArrayList<String> slist = new ArrayList<String>();
    private Spinner spinnerInstrTypes;
    private Spinner spinnerStr;
    CheckBox acousticCheckBox;
    boolean isAcoustic = false;
    ArrayAdapter<String> dataAdapterStr;
    ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        iBrand = (EditText) findViewById(R.id.newInstrBrandName);
        iModel = (EditText) findViewById(R.id.newInstrModelName);
        acousticCheckBox = findViewById(R.id.acousticCheckBox);
        Intent intent = getIntent();        //replyTo = intent.getStringExtra("fromActivity");
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        acousticCheckBox.setChecked(isAcoustic);

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
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // create a new adapter with the corresponding values
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
    // add items into spinner for instrument types
    public void addItemsOnInstrTypesSpinner(){
        spinnerInstrTypes = (Spinner) findViewById(R.id.instrTypeSpinner);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
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
        isAcoustic = ((CheckBox) view).isChecked();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String appState, instState, strState;

        // this code should be ok, I have to write the code in AddNewString.java to return this properly
        if (requestCode == ADD_NEW_STRING_REQUEST){
            if (resultCode == RESULT_OK){
                // grab info from inbox
                appState = data.getStringExtra("appstate");
                instState = data.getStringExtra("inststate");
                strState = data.getStringExtra("strstate");
                String instrBrandName = data.getStringExtra("instrBrandName");
                String instrModelName = data.getStringExtra("instrModelName");
                isAcoustic = data.getBooleanExtra("isAcoustic", false); // make sure this works
                String instrTypeLowercase = data.getStringExtra("instrTypeLowercase");
                int strID = data.getIntExtra("strID", 0); // TODO: Figure out how to identify which string we just added, to update str spinner selection (ie. ID? If so, we need to grab the ID in AddNewString)

                // set variables with what we just grabbed
                iBrand.setText(instrBrandName);
                iModel.setText(instrModelName);
                acousticCheckBox.setChecked(isAcoustic);
                A1.setAppState(appState);  // Restore data object states on return
                I1.setInstState(instState);
                S1.setStrState(strState);
                // update string list spinner accordingly
                try {
                    slist.clear();
                    slist = S1.getStringsStrList(context, I1.getType());
                    addItemsStrSpinner();
                } catch (SQLException throwables) { // TODO: Check if throwables should be spelled throwable?
                    throwables.printStackTrace();
                }

                spinnerInstrTypes.setSelection(dataAdapter.getPosition(instrTypePropercase));
                dataAdapterStr.notifyDataSetChanged(); // TODO: Check to see if this goes after setting the str selection or before it
                // TODO: Set selection of string spinner to the new string we just added here
            }
        }
    }

    // returns new instrument data back to activity it came from
    public void addNewInstr(View view){
        String instrBrandName = iBrand.getText().toString();
        String instrModelName = iModel.getText().toString();

        Intent resultIntent = new Intent();
        // TODO: Then, I1.setAcoustic() to true or false depending on boolean isAcoustic value
        // TODO: Add instr info (brandName + modelName + instrType + isAcoustic + attachedString, etc) into an instr object and add into DB (look into config to see how this is done)
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // takes user to AddNewString.java
    public void addNewStr(View view){
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        // TODO: sends the new instr info over to AddNewString.java (done), which will pass it back on return (need to do)
        String instrBrandName = iBrand.getText().toString();
        String instrModelName = iModel.getText().toString();
        String instrTypeLowercase = spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString().toLowerCase();

        Intent intent = new Intent(context, AddNewStringFromAddNewInstr.class);
        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        intent.putExtra("instrBrandName", instrBrandName);
        intent.putExtra("instrModelName", instrModelName);
        intent.putExtra("isAcoustic", isAcoustic);
        intent.putExtra("instrTypeLowercase", instrTypeLowercase);
        startActivityForResult(intent, ADD_NEW_STRING_REQUEST);
    }

}
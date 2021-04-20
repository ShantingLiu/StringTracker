
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
    boolean isAcoustic = false;
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
                I1.setType(spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString().toLowerCase());
                System.out.println("I1 type = "+ I1.getType());

                S1.loadStrings(I1.getStringsID(), context); // maybe we don't need this line
                slist.clear();
                try {
                    slist = S1.getStringsStrList(context, I1.getType());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                System.out.println("slist length = " + slist.size());
                for (String str : slist){
                    System.out.println(str);
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
    public void onCheckBoxClicked(View view){ // TODO: Needs to read boolean isAcoustic, and when the activity loads, if isAcoustic is true, the checkbox should start off checked
        // Is the view now checked?
        acoustic = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (acoustic){
            // TODO: set acoustic to true ((Am I doing this right?))
            isAcoustic = true;
        } else {
            isAcoustic = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String appState, instState, strState;

        // this code should be ok, I have to write the code in AddNewString.java to return this properly
        if (requestCode == ADD_NEW_STRING_REQUEST){
            if (resultCode == RESULT_OK){
                appState = data.getStringExtra("appstate");
                instState = data.getStringExtra("inststate");
                strState = data.getStringExtra("strstate");

                data.getStringExtra("instrBrandName");
                data.getStringExtra("instrModelName");
                data.getBooleanExtra("isAcoustic", false); // make sure this works
                data.getStringExtra("instrTypeLowercase");
                data.getIntExtra("strID", 0); // TODO: Figure out how to identify which string we just added (ie. ID? If so, we need to grab the ID in AddNewString)

                A1.setAppState(appState);  // Restore data object states on return
                I1.setInstState(instState);
                S1.setStrState(strState);
                try {
                    slist.clear();
                    slist = S1.getStringsStrList(context, I1.getType());
                    addItemsStrSpinner();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataAdapterStr.notifyDataSetChanged();
            }
        }
    }

    // returns new instrument data back to activity it came from
    public void addNewInstr(View view){
        String instrBrandName = newInstrBrandNamePrompt.getText().toString();
        String instrModelName = newInstrModelNamePrompt.getText().toString();

        Intent resultIntent = new Intent();
        // TODO: Add instr info (brandName + modelName + instrType, etc) into an instr object and add into DB (look into config to see how this is done)
        // TODO: Then, I1.setAcoustic() to true or false depending on boolean isAcoustic value
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
        // TODO: sends the new instr info over to AddNewString.java, which will pass it back on return
        // TODO: need to get brand/model/etc out of onActivityResult after it's passed back
        String instrBrandName = newInstrBrandNamePrompt.getText().toString();
        String instrModelName = newInstrModelNamePrompt.getText().toString();
        // isAcoustic needs to be passed as well
        String instrTypeLowercase = spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString().toLowerCase();
        //
        Intent intent = new Intent(context, AddNewString.class);

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
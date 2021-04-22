package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Configuration2 extends AppCompatActivity {
    public static final int ADD_NEW_INSTR_REQUEST = 1;
    public static final int EDIT_INSTR_REQUEST = 2;
    public static final int NEW_INSTR_REQUEST = 3;
    public static final int ADD_NEW_STR_REQUEST = 4;
    // Main variables
    private static final String LOG_TAG = Configuration2.class.getSimpleName();
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = Configuration2.this;

    EditText iBrand;
    EditText iModel;
    EditText iInstID;
    EditText sBrand;
    EditText sModel;
    EditText sStrID;
    TextView configTextView;

    // Spinner variables
    private ArrayAdapter<String> dataAdapter;
    private Spinner spinner1;
    private Spinner spinner2;  // *** Spinner for String sets
    private ArrayList<String> stringsList = new ArrayList<>();  // *** ArrayList for StringSets

    private Button buttonRet;  // *** needed to pass info back to main
    private Button addNewInstrButton;
    private ArrayList<String> instrList = new ArrayList<>();
    private ArrayList<String> addedInstruments = new ArrayList<>();

    private String currInstName;
    private int currInstIndex;
    private int newStringsID = -1;

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
        setContentView(R.layout.activity_configuration2);

        String appState;
        String instState;
        String strState;
        Intent mIntent = getIntent();
        appState = mIntent.getStringExtra("appstate");
        instState = mIntent.getStringExtra("inststate");
        strState = mIntent.getStringExtra("strstate");

        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        configTextView = (TextView) findViewById(R.id.configTextView);
        iBrand = (EditText) findViewById(R.id.editTextBrand);
        iModel = (EditText) findViewById(R.id.editTextModel);
        iInstID = (EditText) findViewById(R.id.editTextInstrID);
        sBrand = (EditText) findViewById(R.id.editTextStrBrand);
        sModel = (EditText) findViewById(R.id.editTextStrModel);
        sStrID = (EditText) findViewById(R.id.editTextStrID);

        int spinnercnt = 0;
        //updateDisplay();
        try {       // *** with DB access we need to deal with exceptions
            populateList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        addItemsOnSpinner1();
        addListenerOnSpinnerItemSelection();

        addItemsOnSpinner2();  //  *** Copies of Spinner1 code
        addListenerOnSpinner2ItemSelection();

        addListenerOnButton();

        ////////////////////////////////////////////////////////////////////////////////////
        // TODO  set starting position for spinner not working

        spinner1.setSelection(findPosition(instrList, I1.getInstrID()));
        spinner2.setSelection(2);//findPosition(stringsList, I1.getStringsID()));
        // *** Actions upon selection for spinners
        // Select Instrument

        // TODO  *** NOTICED: spinner1 OnItemSelected() is executing once and spinner2 OnItemSelected() is executed twice by simple navingation to Config2 and returning to main
        // TODO Fix these actions without click event since it is executing a STRING CHANGE EVENT when no strings were selected by user.
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String tmp = instrList.get(position);
                String token = tmp.split(":")[1];
                int newinstrid = Integer.parseInt(token.split(" ")[0].trim());
                if(userIsInteracting) {
                    // set new instrumentID load new Instrument and StringSet from DB
                    A1.setInstrID(newinstrid);
                    I1.loadInstr(newinstrid, context);  // get selected instr from DB
                    S1.loadStrings(I1.getStringsID(), context);  // get selected string set from DB

                    // Need to update the strings spinner2 to match possibly new type of instrument
                    stringsList.clear();
                    try {
                        stringsList = S1.getStringsStrList(context, I1.getType());  // *** gets Strings ArrayList for DB based on the Type of instrument selected in I1
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    addItemsOnSpinner2();

                    saveState();  // be sure run state changes saved
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        // spinner2 Select StringSet action with StringSet Change Sequence
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                 String tmp = stringsList.get(position);
                String token = tmp.split(":")[1];
                // int newstringsid = Integer.parseInt(token.split(" ")[0].trim());
                newStringsID = Integer.parseInt(token.split(" ")[0].trim());

             /*   // *** Stops false select in spinner2
                System.out.println("### userIsInteracting:"+userIsInteracting);
                if(userIsInteracting) {
                    // Update Instrument and load new StringSet from DB
                    // NOTE: This is a String Change Event!
                    /// STRINGSET CHANGE EVENT Sequence ////
                    I1.logStringChange();
                    // do not attempt to update AvgSent if there are no sessions
                    if (I1.getPlayTime() > 0 && I1.getSessionCnt() > 0) {

                        S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());
                        System.out.println("AvgProj:"+S1.getAvgProjStr());  // DEBUG show updated avgs
                        System.out.println("AvgTone:"+S1.getAvgToneStr());
                        System.out.println("AvgInton:"+S1.getAvgIntonStr());

                        // removed testmode save of sent logs
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                     }
                    S1.updateStrings(S1.getStringsID(), context);  // update DB!

                    System.out.println("*** STRING CHANGE *** new stringsID=" + I1.getStringsID());  // DEBUG

                    // set new instrumentID load new Instrument and StringSet from DB
                    I1.setStringsID(newstringsid);
                    S1.loadStrings(I1.getStringsID(), context);
                    I1.init();   // clear for new string cycle
                    I1.updateInstr(I1.getInstrID(), context);  // be sure to update DB item for new strings selected
                    A1.init();  // clear internal time values

                    saveState();  // be sure changes are saved
                } */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        // Return button - a good idea to keep this with state passing intact
        buttonRet = findViewById(R.id.buttonReturn);
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

    } ///////// OnCreate() /////////////////////////////////

    // *** Quick search for id position in array list
    private int findPosition(ArrayList <String> x, int id){
        int pos = 0;
        String s;
        for(int i = 0; i < x.size(); ++i){
            s = x.get(i);
            String token = s.split(":")[1];
            int tmpid = Integer.parseInt(token.split(" ")[0].trim());
            if(id == tmpid){
                pos = i;
            }
        }
        return pos;
    }

    // New method set to the OnClick for buttonChangeStr in xml
    public void changeStrings(View view){
        // Update Instrument and load new StringSet from DB
        // NOTE: This is a String Change Event!
        /// STRINGSET CHANGE EVENT Sequence ////
        if(newStringsID < 0){   // if not set to a valid ID with spinner use existing stringsID
            newStringsID = I1.getStringsID();
        }
        I1.logStringChange();
        // do not attempt to update AvgSent if there are no sessions
        if (I1.getPlayTime() > 0 && I1.getSessionCnt() > 0) {

            S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());
            System.out.println("AvgProj:"+S1.getAvgProjStr());  // DEBUG show updated avgs
            System.out.println("AvgTone:"+S1.getAvgToneStr());
            System.out.println("AvgInton:"+S1.getAvgIntonStr());

            // removed testmode save of sent logs
            try {
                I1.clearSentLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        S1.updateStrings(S1.getStringsID(), context);  // update DB!

        System.out.println("*** STRING CHANGE *** new stringsID=" + I1.getStringsID());  // DEBUG

        // set new instrumentID load new Instrument and StringSet from DB
        I1.setStringsID(newStringsID);
        S1.loadStrings(I1.getStringsID(), context);
        I1.init();   // clear for new string cycle
        I1.updateInstr(I1.getInstrID(), context);  // be sure to update DB item for new strings selected
        A1.init();  // clear internal time values

        saveState();  // be sure changes are saved

    }


    // Method updates EditTexts for new instrument or strings
//    void updateDisplay(){
//        iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
//        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
//        iModel.setText(I1.getModel());
//
//        sStrID.setText(String.valueOf(S1.getStringsID()));  // EXAMPLE loading data object values in editText
//        sBrand.setText(S1.getBrand());  // EXAMPLE loading data object values in editText
//        sModel.setText(S1.getModel());
//    }

    // DEBUG METHOD to populates Instrument and StringSet Lists
    // *** Need to re-populate the lists if a new instrument is selected so may want to split into 2 functions
    public void populateList() throws SQLException {
        Toast.makeText(Configuration2.this, "onCreate() populating instrList", Toast.LENGTH_SHORT).show();
        instrList.clear();
        stringsList.clear();
        instrList = I1.getInstrStrList(context);     // *** gets instrument ArrayList for DB
        stringsList = S1.getStringsStrList(context, I1.getType());  // *** gets Strings ArrayList for DB based on the Type of instrument selected in I1
    }

    // add items to spinner dynamically
    public void addItemsOnSpinner1() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }

    // add items to spinner dynamically
    public void addItemsOnSpinner2() {
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    void updateSpinners() throws SQLException {
        populateList();
        addItemsOnSpinner1();
        addItemsOnSpinner2();
    }

    void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // *** copy of OnItemSelect function above.. may be able to combine with above
    void addListenerOnSpinner2ItemSelection() {
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        addNewInstrButton = (Button) findViewById(R.id.addNewInstrButton);

        addNewInstrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // code for add instrument
                Log.d(LOG_TAG, "Add New Instrument Button clicked!");
                Intent intent = new Intent(getApplicationContext(), AddNewInstrument.class);
                String appState = A1.getAppState(); // ***
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // *** forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);

                startActivityForResult(intent, ADD_NEW_INSTR_REQUEST);
            }

        });
    }
    // TODO: onActivityResult() will be very long; I may refactor into smaller sub-functions
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        userIsInteracting = false;  // Added to keep the bleeping spinners from autonomously selecting upon return from edit screens

        String appState, instState, strState;
        appState = data.getStringExtra("appstate");
        instState = data.getStringExtra("inststate");
        strState = data.getStringExtra("strstate");
        A1.setAppState(appState);  // Restore data object states on return
        I1.setInstState(instState);
        S1.setStrState(strState);
        //System.out.println("*** Ret2Config2 Instrument State:"+instState);

        // New instrument added
        if (requestCode == ADD_NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    updateSpinners();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataAdapter.notifyDataSetChanged();
            }
        }

        // Instrument edited or deleted
        if (requestCode == EDIT_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                String replyInstruction =
                        data.getStringExtra("replyInstruction");
                if (replyInstruction.equals("000000000")){ // delete instrument command
                    I1.delInstr(context, I1.getInstrID());
                    dataAdapter.notifyDataSetChanged();
                    Toast.makeText(Configuration2.this, "Deleted instrument \"" + currInstName + "\"", Toast.LENGTH_SHORT).show();
                    // TODO: Make user select a new Instrument w/ button to add a new Instrument+String
                    promptSelectNewInstr(); //TODO -debug not getting here
                } else { // update instrument command
                    dataAdapter.notifyDataSetChanged();
                    Toast.makeText(Configuration2.this, "Updated previous instrument \"" + currInstName + "\" to \"" + replyInstruction + "\"", Toast.LENGTH_SHORT).show();
                }
                try {
                    updateSpinners();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }

        // When a user deletes an instrument and then selects a string, this code then updates the spinners and states
        if (requestCode == NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                addedInstruments = data.getStringArrayListExtra("addInstrList");
                int newCurrInstIndex = data.getIntExtra("selInstrIndex", 0);
                addInstrs(addedInstruments);
                dataAdapter.notifyDataSetChanged();
                spinner1.setSelection(newCurrInstIndex);
            }
        }

        // TODO: New String Selected - links the result from launchEditInstrument()'s call to AddNewStringFromConfig.java
    }

    // *** Helper to store AppState, Instrument, and StringSet states
    public void saveState(){
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();  // be sure we have a copy of states stored
    }

    public void addInstrs(ArrayList<String> arr){
        instrList.addAll(arr);
    }

    // Force user to select a new instrument after deletion of an instrument
    public void promptSelectNewInstr(){
        Log.d(LOG_TAG, "Prompting user to select a new instrument");
        Intent intent = new Intent(this, SelNewInstrument.class);
        String appState = A1.getAppState(); // ***
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);

        startActivityForResult(intent, NEW_INSTR_REQUEST);
    }

    /*  edit function that gets the name and index of the currently selected spinner item
        and goees into an edit screen and passes the value of the return back to the index
    */

    public void launchEditInstrument(View view){
        Log.d(LOG_TAG, "Edit Instrument Button clicked!");
        String appState = A1.getAppState(); // ***
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        currInstIndex = spinner1.getSelectedItemPosition();
        currInstName = instrList.get(currInstIndex);
        System.out.println("Sending isAcoustic = " + I1.getAcoustic());

        Intent intent = new Intent(this, EditInstrument.class);

        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        intent.putExtra("iName", currInstName);
        startActivityForResult(intent, EDIT_INSTR_REQUEST);
    }

    // "Add New" button for Strings - launches AddNewStringFromConfig.java
    // TODO: Write the corresponding onActivityResult() tidbit to grab the return of a new string
    // TODO: Update the Selected Strings spinner to the newest added string
    public void launchAddNewStr(View view){
        Log.d(LOG_TAG, "Add New String (from Config) Button clicked!");
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        currInstIndex = spinner1.getSelectedItemPosition();
        currInstName = instrList.get(currInstIndex);

        Intent intent = new Intent(this, AddNewStringFromConfig.class);

        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        intent.putExtra("iName", currInstName);
        startActivityForResult(intent, ADD_NEW_STR_REQUEST);
    }

    //TODO: Create an activity for Editing strings, create the method here, add the tidbit to onActivityResult()

}

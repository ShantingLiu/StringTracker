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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Configuration2 extends AppCompatActivity {
    public static final int ADD_NEW_INSTR_REQUEST = 1;
    public static final int EDIT_INSTR_REQUEST = 2;
    public static final int NEW_INSTR_REQUEST = 3;
    public static final int ADD_NEW_STR_REQUEST = 4;
    public static final int EDIT_STR_REQUEST = 5;
    // Main variables
    private static final String LOG_TAG = Configuration2.class.getSimpleName();
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = Configuration2.this;

    TextView configTextView;

    // Spinner variables
    private ArrayAdapter<String> dataAdapter;
    private ArrayAdapter<String> strDataAdapter;
    private Spinner spinner1;
    private Spinner spinner2;  // *** Spinner for String sets
    private ArrayList<String> stringsList = new ArrayList<>();  // *** ArrayList for StringSets

    private Button buttonRet;  // *** needed to pass info back to main
    private Button addNewInstrButton;
    private Button buttonSavePrefs;  // Preferences
    private CheckBox checkBoxEnSent;
    private CheckBox checkBoxTestMode;
    private EditText editTextMaxSess;

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
        I1.loadInstr(I1.getInstrID(), context);
        System.out.println("Acoustic STATE:"+I1.getAcoustic());

        configTextView = (TextView) findViewById(R.id.configTextView);   // Prefs
        editTextMaxSess = (EditText) findViewById(R.id.editTextMaxSess);
        checkBoxEnSent = (CheckBox) findViewById(R.id.checkBoxEnSent);
        checkBoxTestMode = (CheckBox) findViewById(R.id.checkBoxTestMode);
        editTextMaxSess.setText(String.valueOf(A1.getMaxSessionTime()));
        checkBoxEnSent.setChecked(A1.getEnableSent());
        checkBoxTestMode.setChecked(A1.getTestMode());

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
        spinner2.setSelection(findPosition(stringsList, I1.getStringsID()));
        // *** Actions upon selection for spinners
        // Select Instrument

        // TODO  *** NOTICED: spinner1 OnItemSelected() is executing once and spinner2 OnItemSelected() is executed twice by simple navingation to Config2 and returning to main
        // TODO Fix these actions without click event since it is executing a STRING CHANGE EVENT when no strings were selected by user.
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(userIsInteracting) {
                    String tmp = instrList.get(position);
                    String token = tmp.split(":")[1];
                    int newinstrid = Integer.parseInt(token.split(" ")[0].trim());
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
                if(userIsInteracting) {
                    String tmp = stringsList.get(position);
                    String token = tmp.split(":")[1];
                    newStringsID = Integer.parseInt(token.split(" ")[0].trim());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        // Return button - a good idea to keep this with state passing intact
        buttonSavePrefs = findViewById(R.id.buttonSavePrefs);
        buttonSavePrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                A1.setEnableSent(checkBoxEnSent.isChecked() );
                A1.setTestMode(checkBoxTestMode.isChecked());
                A1.setMaxSessionTime(Integer.parseInt(String.valueOf(editTextMaxSess.getText())));
                saveState();
                Toast.makeText(Configuration2.this, "Preferences Saved!", Toast.LENGTH_LONG).show();
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
                saveState();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        checkBoxEnSent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Toast.makeText(Configuration2.this, "Ratings:  "
                        + String.format("Click SAVE PREFS to  save this setting!"), Toast.LENGTH_LONG).show();
            }
        });

        checkBoxTestMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Toast.makeText(Configuration2.this, "Ratings:  "
                        + String.format("Click SAVE PREFS to  save this setting!"), Toast.LENGTH_LONG).show();
            }
        });

    } ///////// OnCreate() /////////////////////////////////

    // *** Quick search for id position in array list
    private int findPosition(ArrayList <String> x, int id){
        if (x == null){
            return 0;
        }
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

    //// New method set to the OnClick for buttonChangeStr in xml ///
    // No longer takes this important action on a glitchy spinner OnItemSelect
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
        I1.setChangeTimeStamp(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));
        saveState();  // be sure changes are saved
        Toast.makeText(Configuration2.this, "Ratings:  "
                + String.format("String Change Event Executed!"), Toast.LENGTH_LONG).show();

    }

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
        strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringsList);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(strDataAdapter);
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
                currInstIndex = spinner1.getSelectedItemPosition();
                if(instrList.size()>0){
                    currInstName = instrList.get(currInstIndex);
                } else {
                    currInstName = "none";
                }
                intent.putExtra("iName", currInstName);
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
        saveState();  // save state just in case
        editTextMaxSess.setText(String.valueOf(A1.getMaxSessionTime()));
        checkBoxEnSent.setChecked(A1.getEnableSent());
        checkBoxTestMode.setChecked(A1.getTestMode());

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
                strDataAdapter.notifyDataSetChanged();

                int newInstrId = I1.getInstrID();
                int newStrId = S1.getStringsID();

                spinner1.setSelection(findPosition(instrList, newInstrId));
                spinner2.setSelection(findPosition(stringsList, newStrId));
            }
        }

        // Instrument edited or deleted
        if (requestCode == EDIT_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                String replyInstruction =
                        data.getStringExtra("replyInstruction");
                if (replyInstruction.equals("000000000")){ // delete instrument command
                    System.out.println("Delete Instrument command received");
                    I1.delInstr(context, I1.getInstrID());
                    try {
                        updateSpinners();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    dataAdapter.notifyDataSetChanged(); // should this go above updateSpinners try-catch?
                    strDataAdapter.notifyDataSetChanged();
                    Toast.makeText(Configuration2.this, "Deleted instrument \"" + currInstName + "\"", Toast.LENGTH_SHORT).show();
                    // TODO: Make user select a new Instrument w/ button to add a new Instrument+String

                    promptSelectNewInstr(); //TODO: DEBUG - not getting here
                } else { // update instrument command

                    try {
                        updateSpinners();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    dataAdapter.notifyDataSetChanged(); // should this go above updateSpinners try-catch?
                    strDataAdapter.notifyDataSetChanged();

                    int newInstrId = I1.getInstrID();
                    int newStrId = S1.getStringsID();

                    spinner1.setSelection(findPosition(instrList, newInstrId));
                    spinner2.setSelection(findPosition(stringsList, newStrId));
                }
            }
        }

        // When a user deletes an instrument and then selects a string, this code then updates the spinners and states
        if (requestCode == NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    updateSpinners();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataAdapter.notifyDataSetChanged();
                strDataAdapter.notifyDataSetChanged();

                int newInstrId = I1.getInstrID();
                int newStrId = S1.getStringsID();

                spinner1.setSelection(findPosition(instrList, newInstrId));
                spinner2.setSelection(findPosition(stringsList, newStrId));
            }
        }

        // TODO: New String Added - links the result from launchAddNewStr()'s call to AddNewStringFromConfig.java
        if (requestCode == ADD_NEW_STR_REQUEST) {
            if (resultCode == RESULT_OK) {
                // TODO: Finish up code here
                try {
                    updateSpinners();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataAdapter.notifyDataSetChanged();
                strDataAdapter.notifyDataSetChanged();
                int newInstrId = I1.getInstrID();
                int newStrId = S1.getStringsID();

                spinner1.setSelection(findPosition(instrList, newInstrId));
                spinner2.setSelection(findPosition(stringsList, newStrId));
            }
        }

        // When a user returns from EditString called from Config
        // TODO: Check if this works
        if (requestCode == EDIT_STR_REQUEST){
            if (resultCode == RESULT_OK) {
                try {
                    updateSpinners();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataAdapter.notifyDataSetChanged();
                strDataAdapter.notifyDataSetChanged();
                int newInstrId = I1.getInstrID();
                int newStrId = S1.getStringsID();

                spinner1.setSelection(findPosition(instrList, newInstrId));
                spinner2.setSelection(findPosition(stringsList, newStrId));
            }
        }
    }

    // *** Helper to store AppState, Instrument, and StringSet states
    public void saveState(){
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();  // be sure we have a copy of states stored
    }

    //public void addInstrs(ArrayList<String> arr){ // delete this?
    //    instrList.addAll(arr);
    //}

    // Force user to select a new instrument after deletion of an instrument
    public void promptSelectNewInstr(){
        System.out.println("promptSelectNewInstr() requesting proc");
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
//        currInstIndex = spinner1.getSelectedItemPosition();
//        currInstName = instrList.get(currInstIndex);
        System.out.println("Sending isAcoustic = " + I1.getAcoustic());

        Intent intent = new Intent(this, EditInstrument.class);

        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
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
        if(instrList.size()>0){
            currInstName = instrList.get(currInstIndex);
        } else {
            currInstName = "none";
        }

        Intent intent = new Intent(this, AddNewStringFromConfig.class);

        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        // While these below variables are not necessary to pass in this context, AddNewStringFromAddNewInstr expects it
        intent.putExtra("instrBrandName", I1.getBrand());
        intent.putExtra("instrModelName", I1.getModel());
        intent.putExtra("isAcoustic", I1.getAcoustic());
        intent.putExtra("instrType", I1.getType());
        //
        intent.putExtra("iName", currInstName);
        startActivityForResult(intent, ADD_NEW_STR_REQUEST);
    }

    //TODO: Create an activity for Editing strings, create the method here, add the tidbit to onActivityResult()
    public void launchEditStr(View view){
        Log.d(LOG_TAG, "Edit String (from Config) Button clicked!");
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        currInstIndex = spinner1.getSelectedItemPosition();
        currInstName = instrList.get(currInstIndex);

        Intent intent = new Intent(this, EditString.class);

        intent.putExtra("appstate", appState);   // *** forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        intent.putExtra("iName", currInstName);
        startActivityForResult(intent, EDIT_STR_REQUEST);
    }

}

package com.example.stringtracker;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.stringtracker.MainActivity.TEXT_REQUEST;

// Configuration Activity class for test/debug purposes  WKD
public class Configuration extends AppCompatActivity {
    private static final String LOG_TAG =
            Configuration.class.getSimpleName();
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
    Button buttonUpd;
    Button buttonRandInstr;
    Button buttonSelInstr;
    Button buttonDel;

    EditText iBrand;
    EditText iModel;
    EditText iInstID;
    EditText sBrand;
    EditText sModel;
    EditText sStrID;

    TextView configTextView;
    Context context = Configuration.this;

    ///// For New Strings ListView on screen  ////
    ListView lv_strings2;
    ArrayAdapter ad;
    ArrayList<String> slist = new ArrayList<String>();

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

//--------------- Code to implement Strings ListView in Config screen
        lv_strings2  = findViewById(R.id.lv_strings2);
        try {
            slist = S1.getStringsStrList(context, I1.getType());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, slist);
        lv_strings2.setAdapter(ad);

        // String Selection From ListView - NOTE: THIS IS A STRING CHANGE EVENT!
        lv_strings2.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Configuration.this, "pos="+position+" name="+ilist.get(position), Toast.LENGTH_SHORT).show();
                String tmp = slist.get(position);
                String token = tmp.split(":")[1];
                int newStringsID = Integer.parseInt(token.split(" ")[0].trim());

                /// String Change Event Sequence ////
                I1.logStringChange();
                // do not attempt to update if there are no sessions
                if (I1.getPlayTime() > 0  && I1.getSessionCnt() > 0) {
                    // Update avg sentiment values for the old strings
                    S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());

                    if (!A1.getTestMode()) {  // if in normal operating mode clear sent log
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Set to New StringsID load new StringSet from DB
                I1.setStringsID(newStringsID);
                S1.loadStrings(newStringsID, context);
                // reset the counters
                A1.init();
                I1.init();
                // save changes in AppState file
                A1.setStrState(S1.getStrState());
                A1.setInstState(I1.getInstState());
                A1.saveRunState();  // be sure run state changes saved to file

                // update display
                updateDisplay();  // updates EditTexts
                String selInstText = "String Change - New Strings ID:"+S1.getStringsID()+" "+S1.getBrand()+"-"+S1.getModel();
                Toast.makeText(Configuration.this, selInstText, Toast.LENGTH_SHORT).show();
            }
        });
//----------------------------------------------------

        ////////// EXAMPLE DEBUG CODE WITH DB ACCESS - EditText and save button //////////
        configTextView = (TextView) findViewById(R.id.configTextView);
        iBrand = (EditText) findViewById(R.id.editTextBrand);
        iModel = (EditText) findViewById(R.id.editTextModel);
        iInstID = (EditText) findViewById(R.id.editTextInstrID);
        sBrand = (EditText) findViewById(R.id.editTextStrBrand);
        sModel = (EditText) findViewById(R.id.editTextStrModel);
        sStrID = (EditText) findViewById(R.id.editTextStrID);

        updateDisplay();

        ////// BUTTON SELECT INSTRUMENT  //////
        buttonSelInstr = findViewById(R.id.buttonSelInstr2);
        buttonSelInstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSelectInstrument(v);
            }
        });

        // ///// BUTTON INSERT NEW INSTR TO DB /////
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I1.setInstrID(Integer.parseInt(iInstID.getText().toString()));
                I1.setBrand(iBrand.getText().toString());
                I1.setModel(iModel.getText().toString());
                S1.setStringsID(Integer.parseInt(sStrID.getText().toString()));
                S1.setBrand(sBrand.getText().toString());
                S1.setModel(sModel.getText().toString());

                Context context = Configuration.this;
                if(I1.insertInstr(context) && S1.insertStrings(context)){
                    showToast(v);
                }

                A1.setInstrumentCnt(I1.getInstrStrList(context).size());
                try {
                    A1.setStringsCnt(S1.getStringsStrList(context, "?").size());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        });

        // TODO - Check that all data written to DB is correctly stored and retrieved

        ////// BUTTON LOAD INSTR FROM DB //////
        buttonLoad = findViewById(R.id.buttonLoad);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Configuration.this;
                System.out.println("*** Config Call DBLoad instrID = "+Integer.parseInt(iInstID.getText().toString()));

                if(I1.loadInstr(Integer.parseInt(iInstID.getText().toString()), context)){
                    A1.setInstrID(Integer.parseInt(iInstID.getText().toString()));  // be sure to update AppState
                    showToast(v);
                }

                if(S1.loadStrings(Integer.parseInt(sStrID.getText().toString()), context)){
                    I1.setStringsID(Integer.parseInt(sStrID.getText().toString()));  // be sure to update Instrument
                    showToast(v);
                }

                updateDisplay();
            }
        });

        ////// BUTTON UPDATE INSTR IN  DB //////
        buttonUpd = findViewById(R.id.buttonUpd);
        buttonUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Configuration.this;
                System.out.println("*** Config Call DBUpdate instrID = "+Integer.parseInt(iInstID.getText().toString()));
                A1.setInstrID(Integer.parseInt(iInstID.getText().toString()));  // be sure to update AppState
                I1.setInstrID(Integer.parseInt(iInstID.getText().toString()));
                I1.setBrand(iBrand.getText().toString());
                I1.setModel(iModel.getText().toString());
                S1.setStringsID(Integer.parseInt(sStrID.getText().toString()));
                S1.setBrand(sBrand.getText().toString());
                S1.setModel(sModel.getText().toString());

                if(I1.updateInstr(Integer.parseInt(iInstID.getText().toString()), context) && S1.updateStrings(Integer.parseInt(sStrID.getText().toString()), context)){
                    showToast(v);  // the toast indicates that it worked
                }
                System.out.println("** UPDATED InstrID = "+I1.getInstrID()+" Brand = "+I1.getBrand()+" Model = "+I1.getModel());
                System.out.println("** UPDATED StringsID = "+S1.getStringsID()+" Brand = "+S1.getBrand()+" Model = "+S1.getModel());
            }
        });

        ////// BUTTON DELETE INSTR IN  DB //////
        buttonDel = findViewById(R.id.buttonDel);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Configuration.this;
                System.out.println("*** Config Call DBDelete instrID = "+Integer.parseInt(iInstID.getText().toString())+"  A1.instrCnt="+A1.getInstrumentCnt());

                int nextInstrID = I1.getInstrID();
                I1.delInstr(context, I1.getInstrID());  // Delete DB entry
                if(A1.getInstrumentCnt() > 0) {  // decrement total instrument count in AppState
                    A1.setInstrumentCnt(A1.getInstrumentCnt() - 1);
                }

                // finally launch select new instrument to force user to choose another
                launchSelectInstrument(v);
                // update AppState
                A1.setInstrID(I1.getInstrID());
                S1.loadStrings(I1.getStringsID(),context);  // update stringset to match new instrument
                updateDisplay();
            }
        });

        // ///// BUTTON GENERATE NEW RANDOM INSTR  /////
        buttonRandInstr = findViewById(R.id.buttonRandInst);
        buttonRandInstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(A1.getTestMode()) {
                    genStrInst();  // allow random instrument & string generation if in TestMode
                }
                A1.setInstrumentCnt(I1.getInstrStrList(context).size());
                try {
                    A1.setStringsCnt(S1.getStringsStrList(context, "?").size());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                updateDisplay();
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

    //  Launcher for Select Strings Screen
    public void launchSelectStrings(View view){
        Log.d(LOG_TAG, "Sel Instrument Button clicked!");
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();

        Intent intent = new Intent(Configuration.this, SelectStrings.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);   // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);
        // button lockout for first rum
        if (!A1.firstRun()) {
            startActivityForResult(intent, TEXT_REQUEST);
        }
    }

    // Launcher for Select Instrument Screen
    public void launchSelectInstrument(View view) {
        Log.d(LOG_TAG, "Sel Instrument Button clicked!");
        A1.setInstState(I1.getInstState());  // update object state strings in AppState
        A1.setStrState(S1.getStrState());
        A1.saveRunState();

        Intent intent = new Intent(this, SelectInstrument.class);
        String appState = A1.getAppState();
        String instState = I1.getInstState();
        String strState = S1.getStrState();
        intent.putExtra("appstate", appState);    // forward object states
        intent.putExtra("inststate", instState);
        intent.putExtra("strstate", strState);

        // button lockout for first rum
        if (!A1.firstRun()) {
            startActivityForResult(intent, TEXT_REQUEST);
        }

    }

    // Method updates EditTexts for new instrument or strings
    void updateDisplay(){
        iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
        iModel.setText(I1.getModel());

        sStrID.setText(String.valueOf(S1.getStringsID()));  // EXAMPLE loading data object values in editText
        sBrand.setText(S1.getBrand());  // EXAMPLE loading data object values in editText
        sModel.setText(S1.getModel());
        
        // repopulate Strings ListView
        try {
            slist = S1.getStringsStrList(context, I1.getType());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, slist);
        lv_strings2.setAdapter(ad);

    }

    // A handy toast Saved message you might want to use
    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.toast_message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    // Method receives results from String Select
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String appState;
        String instState;
        String strState;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                appState = data.getStringExtra("appstate");
                instState = data.getStringExtra("inststate");
                strState = data.getStringExtra("strstate");
                //A1.loadRunState();  // DEBUG tests using file for message passing
                A1.setAppState(appState);  // Restore data object states on return
                I1.setInstState(instState);
                S1.setStrState(strState);

                System.out.println("*** RETURN to CONFIG InstrID = "+I1.getInstrID());
                iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
                iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
                iModel.setText(I1.getModel());

                sStrID.setText(String.valueOf(S1.getStringsID()));  // EXAMPLE loading data object values in editText
                sBrand.setText(S1.getBrand());  // EXAMPLE loading data object values in editText
                sModel.setText(S1.getModel());

            }
            // DEBUG MESSAGES
            if (resultCode == RESULT_CANCELED) {
                configTextView.setText("CANCELED!");
                configTextView.setVisibility(View.VISIBLE);
            }

            updateDisplay();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // DEBUG test code randomly generates instrument and stringset values for A1 and S1
    public void genStrInst() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand[] = {"GHS", "D'Addario", "Martin", "Elixir", "Ernie Bal"};
        String sModel[] = {"A-180", "G-42", "Bronze", "Stainless FW", "Slinky"};
        String sTension[] = {"XL", "Light", "Medium", "Heavy"};
        String sType[] = {"guitar", "banjo", "mandolin", "violin", "cello"};

        String iBrand[] = {"Gibson", "Collings", "Fender", "Taylor", "PRS"};
        String iModel[] = {"L5", "D28", "Artist", "F-5", "Yellowstone"};

        int rand_sBr = rand.nextInt(5);
        int rand_sMo = rand.nextInt(5);
        int rand_sTe = rand.nextInt(4);
        int rand_sTy = rand.nextInt(5);
        int rand_sID = rand.nextInt(4)+1;

        int rand_iBr = rand.nextInt(5);
        int rand_iMo = rand.nextInt(5);
        int rand_iID = rand.nextInt(4)+1;

        S1.setStringsID(rand_sID);
        S1.setBrand(sBrand[rand_sBr]);

        S1.setModel(sModel[rand_sMo]);
        S1.setTension(sTension[rand_sTe]);
        S1.setType(sType[rand_sTy]);
        S1.setAvgLife(800);  // set this value for DEBUG test purposes
        S1.setChangeCnt(0);
        S1.setCost(10.0f);

        I1.setInstrID(rand_iID);
        I1.setBrand(iBrand[rand_iBr]);
        I1.setModel(iModel[rand_iMo]);
        I1.setType(sType[rand_sTy]);
        I1.setPlayTime(0);
        I1.setSessionCnt(0);
        I1.setStringsID(rand_sID);  // match up strings with instrument

    }


}
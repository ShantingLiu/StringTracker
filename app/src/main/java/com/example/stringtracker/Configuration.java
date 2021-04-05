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
    Button buttonUpd;
    EditText iBrand;
    EditText iModel;
    EditText iInstID;
    EditText sBrand;
    EditText sModel;
    EditText sStrID;

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

        System.out.println("*** Config OnCreate InstrID = "+I1.getInstrID()+" Brand = "+I1.getBrand());
        System.out.println("*** Config OnCreate StringsID = "+S1.getStringsID()+" Brand = "+S1.getBrand());

        ////////// EXAMPLE DEBUG CODE WITH DB ACCESS - EditText and save button //////////
        iBrand = (EditText) findViewById(R.id.editTextBrand);
        iModel = (EditText) findViewById(R.id.editTextModel);
        iInstID = (EditText) findViewById(R.id.editTextInstrID);
        iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
        iModel.setText(I1.getModel());

        sBrand = (EditText) findViewById(R.id.editTextStrBrand);
        sModel = (EditText) findViewById(R.id.editTextStrModel);
        sStrID = (EditText) findViewById(R.id.editTextStrID);
        sStrID.setText(String.valueOf(S1.getStringsID()));  // EXAMPLE loading data object values in editText
        sBrand.setText(S1.getBrand());  // EXAMPLE loading data object values in editText
        sModel.setText(S1.getModel());



        // ///// INSERT NEW INSTR TO DB /////
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
            }
        });

        // TODO - Check that all data written to DB is correctly stored and retrieved

        ////// LOAD INSTR FROM DB //////
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
                iInstID.setText(String.valueOf(I1.getInstrID()));  // EXAMPLE loading data object values in editText
                iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
                iModel.setText(I1.getModel());

                if(S1.loadStrings(Integer.parseInt(sStrID.getText().toString()), context)){
                    I1.setStringsID(Integer.parseInt(sStrID.getText().toString()));  // be sure to update Instrument
                    showToast(v);
                }
                sStrID.setText(String.valueOf(S1.getStringsID()));  // EXAMPLE loading data object values in editText
                sBrand.setText(S1.getBrand());  // EXAMPLE loading data object values in editText
                sModel.setText(S1.getModel());

                S1.setStringsID(Integer.parseInt(sStrID.getText().toString()));
                S1.setBrand(sBrand.getText().toString());
                S1.setModel(sModel.getText().toString());


            }
        });

        ////// UPDATE INSTR IN  DB //////
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

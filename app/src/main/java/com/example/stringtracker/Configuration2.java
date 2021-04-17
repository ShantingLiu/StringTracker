package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Configuration2 extends AppCompatActivity {
    public static final int ADD_NEW_INSTR_REQUEST = 1;
    public static final int EDIT_INSTR_REQUEST = 2;
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
    private Button addNewInstrButton;
    private ArrayList<String> instrList = new ArrayList<>();

    private String currInstName;
    private int currInstIndex;

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

        //updateDisplay();
        populateList(); // DEBUG method to populate list with instruments

        addItemsOnSpinner1();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
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

    // DEBUG METHOD to populate Instrument List
    void populateList(){
        instrList.add("Cello");
        instrList.add("Piano");
        instrList.add("Electric Guitar");
        instrList.add("Acoustic Guitar");
        instrList.add("Bass");
        instrList.add("Violin");
        instrList.add("Viola");
    }

    void addInstrToList(String instr){
        instrList.add(instr);
    }

    // add items to spinner dynamically
    public void addItemsOnSpinner1() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }

    void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        addNewInstrButton = (Button) findViewById(R.id.addNewInstrButton);

        addNewInstrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Add New Instrument Button clicked!");
                Toast.makeText(Configuration2.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + String.valueOf(spinner1.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), AddNewInstrument.class);
                startActivityForResult(i, ADD_NEW_INSTR_REQUEST);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply =
                        data.getStringExtra(AddNewInstrument.instName);
                addInstrToList(reply);
                dataAdapter.notifyDataSetChanged();
                Toast.makeText(Configuration2.this, "Added new instrument \"" + reply + "\"", Toast.LENGTH_SHORT).show();
            }
        }
    }

/*  edit function that gets the name and index of the currently selected spinner item
    and goees into an edit screen and passes the value of the return back to the index
*/
    public void launchEditInstrument(View view){
        Log.d(LOG_TAG, "Edit Instrument Button clicked!");
        currInstIndex = spinner1.getSelectedItemPosition();
        currInstName = instrList.get(currInstIndex);
        Toast.makeText(Configuration2.this, "Editing instrument \"" + currInstName + "\" at index " + currInstIndex, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EditInstrument.class);
        intent.putExtra("iName", currInstName);
        startActivityForResult(intent, EDIT_INSTR_REQUEST);
    }
}
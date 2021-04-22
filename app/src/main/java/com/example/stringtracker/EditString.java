package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class EditString extends AppCompatActivity {
    private EditText sBrandNameEditText;
    private EditText sModelNameEditText;
    private EditText sCostEditText;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = EditString.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    private final String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    private final String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    private Spinner spinnerStrTension;
    private Spinner spinnerStrInstrType;
    ArrayAdapter<String> DataAdapter;
    ArrayAdapter<String> strDataAdapter;
    String sBrand;
    String sModel;
    String instrType;
    String strTension;
    float strCost;


    @Override
    // TODO: Populate the fields with the values of selected String to edit + set spinners
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_string);

        sBrandNameEditText = (EditText) findViewById(R.id.editStrBrandName);
        sModelNameEditText = (EditText) findViewById(R.id.editStrModelName);
        sCostEditText = (EditText) findViewById(R.id.editStrCost);

        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        sBrand = S1.getBrand();
        sModel = S1.getModel();
        instrType = S1.getType();
        strTension = S1.getTension();
        strCost = S1.getCost();

        addItemsOnStrTensionSpinner();
        addListenerOnStrTensionSpinnerItemSelection();
        addItemsOnStrInstrTypeSpinner();
        addListenerOnStrInstrTypeSpinnerItemSelection();

        sBrandNameEditText.setText(sBrand);
        sModelNameEditText.setText(sModel);
        sCostEditText.setText(String.valueOf(strCost));
        spinnerStrTension.setSelection(strDataAdapter.getPosition(strTension));
        spinnerStrInstrType.setSelection(DataAdapter.getPosition(instrType));

    }

    public void addItemsOnStrTensionSpinner(){
        spinnerStrTension = (Spinner) findViewById(R.id.editStrTensionSpinner);
        strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strTensions);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrTension.setAdapter(strDataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrTensionSpinnerItemSelection(){
        spinnerStrTension = (Spinner) findViewById(R.id.editStrTensionSpinner);
        spinnerStrTension.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addItemsOnStrInstrTypeSpinner(){
        spinnerStrInstrType = (Spinner) findViewById(R.id.editStrInstrTypeSpinner);
        DataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        DataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrInstrType.setAdapter(DataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrInstrTypeSpinnerItemSelection(){
        spinnerStrInstrType = (Spinner) findViewById(R.id.editStrInstrTypeSpinner);
        spinnerStrInstrType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void confirmEditStr(View view){
        sBrand = sBrandNameEditText.getText().toString();
        sModel = sModelNameEditText.getText().toString();
        instrType = spinnerStrInstrType.getSelectedItem().toString(); // TODO: Check if this works
        strTension = spinnerStrTension.getSelectedItem().toString();

        try {
            strCost = Float.parseFloat(sCostEditText.getText().toString());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: NumberFormatException on parsing cost as float in EditString.java");
        }

        Intent resultIntent = new Intent();

        // update String object with new fields
        S1.setBrand(sBrand);
        S1.setModel(sModel);
        S1.setType(instrType);
        S1.setCost(strCost);
        S1.setTension(strTension);

        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        resultIntent.putExtra("appstate", appState);   // *** forward object states
        resultIntent.putExtra("inststate", instState);
        resultIntent.putExtra("strstate", strState);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
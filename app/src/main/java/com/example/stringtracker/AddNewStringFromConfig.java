package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

//TODO: Touch base with Keith to decide if this menu should (1) Assume we're attaching the new added string to the currently selected instrument in config, from which this menu came
// TODO: ^ or (2) Have a spinner for users to select an instrument to attach this string to
// TODO: My vote is for #2

public class AddNewStringFromConfig extends AppCompatActivity {
    private EditText newStrBrandNamePrompt;
    private EditText newStrModelNamePrompt;
    private EditText newStrCostPrompt;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = AddNewStringFromConfig.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    private final String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    private final String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    private Spinner spinnerStrTension;
    private Spinner spinnerStrInstrType;
    String instrBrandName;
    String instrModelName;
    boolean isAcoustic;
    String instrType;
    String iName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_string_from_config);

        newStrBrandNamePrompt = (EditText) findViewById(R.id.newStrBrandName2);
        newStrModelNamePrompt = (EditText) findViewById(R.id.newStrModelName2);
        newStrCostPrompt = (EditText) findViewById(R.id.newStrCost2);

        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");

//        instrType = intent.getStringExtra("instrTypeLowercase");


        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);


        addItemsOnStrTensionSpinner();
        addListenerOnStrTensionSpinnerItemSelection();
        addItemsOnStrInstrTypeSpinner();
        addListenerOnStrInstrTypeSpinnerItemSelection();
    }

    public void addItemsOnStrTensionSpinner(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner2);
        ArrayAdapter<String> strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strTensions);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrTension.setAdapter(strDataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrTensionSpinnerItemSelection(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner2);
        spinnerStrTension.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addItemsOnStrInstrTypeSpinner(){
        spinnerStrInstrType = (Spinner) findViewById(R.id.stringInstrTypeSpinner);
        ArrayAdapter<String> strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrInstrType.setAdapter(strDataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrInstrTypeSpinnerItemSelection(){
        spinnerStrInstrType = (Spinner) findViewById(R.id.stringInstrTypeSpinner);
        spinnerStrInstrType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addNewStr2(View view){
        String strBrandName = newStrBrandNamePrompt.getText().toString();
        String strModelName = newStrModelNamePrompt.getText().toString();
        float strCost;

        try {
            strCost = Float.parseFloat(newStrCostPrompt.getText().toString());
        } catch (NumberFormatException e) {
            strCost = Float.parseFloat("0");
        }

        Intent resultIntent = new Intent();
        // TODO: Add instr info (brandName + modelName + instrType + cost + tension) into a str object and add into DB, linking it to the current instrument
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
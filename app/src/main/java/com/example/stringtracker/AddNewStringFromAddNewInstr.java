
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

// This activity is accessed from the AddNewInstrument.java activity
// We will have a separate activity for the adding new strings from the Configuration activity
public class AddNewStringFromAddNewInstr extends AppCompatActivity {
    private EditText newStrBrandNamePrompt;
    private EditText newStrModelNamePrompt;
    private EditText newStrCostPrompt;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = AddNewStringFromAddNewInstr.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    private final String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    private Spinner spinnerStrTension;
    String instrBrandName;
    String instrModelName;
    boolean isAcoustic;
    String instrTypeLowercase;
    String instrTypePropercase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_string_from_add_new_instr);

        newStrBrandNamePrompt = (EditText) findViewById(R.id.newStrBrandName);
        newStrModelNamePrompt = (EditText) findViewById(R.id.newStrModelName);
        newStrCostPrompt = (EditText) findViewById(R.id.newStrCost);

        //TextView instrTypePrompt = (TextView) findViewById(R.id.strInstrType);

        // get corresponding instrument info from intent
        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");

        instrBrandName = intent.getStringExtra("instrBrandName");
        instrModelName = intent.getStringExtra("instrModelName");
        isAcoustic = intent.getBooleanExtra("isAcoustic", false);
        instrTypeLowercase = intent.getStringExtra("instrTypeLowercase");
        instrTypePropercase = instrTypeLowercase.substring(0, 1).toUpperCase() + instrTypeLowercase.substring(1);


        // set the fields with info we just grabbed from intent
        //instrTypePrompt.setText(instrTypePropercase);
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        // initiate str tension spinner
        addItemsOnStrTensionSpinner();
        addListenerOnStrTensionSpinnerItemSelection();
    }

    public void addItemsOnStrTensionSpinner(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner);
        ArrayAdapter<String> strDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strTensions);
        strDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrTension.setAdapter(strDataAdapter);
    }

    // listener for str tension spinner
    public void addListenerOnStrTensionSpinnerItemSelection(){
        spinnerStrTension = (Spinner) findViewById(R.id.stringTensionSpinner);
        spinnerStrTension.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addNewStr(View view){
        String strBrandName = newStrBrandNamePrompt.getText().toString();
        String strModelName = newStrModelNamePrompt.getText().toString();
        float strCost = Float.parseFloat(newStrCostPrompt.getText().toString());

        Intent resultIntent = new Intent();
        // TODO: Add instr info (brandName + modelName + instrType + cost + tension) into a str object and add into DB, linking it to the current instrument
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        resultIntent.putExtra("appstate", appState);   // *** forward object states
        resultIntent.putExtra("inststate", instState);
        resultIntent.putExtra("strstate", strState);
        // I pass the below info back and forth in case the user is still in the middle of filling out the form, I don't want their changes to be lost in the AddNewInstrument screen when they enter the AddNewString activity
        resultIntent.putExtra("instrBrandName", instrBrandName);
        resultIntent.putExtra("instrModelName", instrModelName);
        resultIntent.putExtra("isAcoustic", isAcoustic);
        resultIntent.putExtra("instrTypeLowercase", instrTypeLowercase);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

}

package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import java.lang.reflect.Array;
import java.util.ArrayList;

// This activity is accessed from the AddNewInstrument.java activity
// We will have a separate activity for the adding new strings from the Configuration activity
public class AddNewString extends AppCompatActivity {
    private EditText newStrBrandNamePrompt;
    private EditText newStrModelNamePrompt;
    private EditText newStrCostPrompt;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = AddNewString.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;
    private String[] strTensions = new String[]{"X-Light", "Light", "Medium", "Heavy"};
    private boolean acoustic = false;
    private Spinner spinnerInstrTypes;
    private Spinner spinnerStrTension;
    CheckBox acousticCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        newStrBrandNamePrompt = findViewById(R.id.newStrBrandName);
        newStrModelNamePrompt = findViewById(R.id.newStrModelName);
        newStrCostPrompt = findViewById(R.id.newStrCost);
        acousticCheckBox = findViewById(R.id.acousticCheckBox);
        Intent intent = getIntent();        //replyTo = intent.getStringExtra("fromActivity");
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
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
        // TODO: Add instr info (brandName + modelName + instrType, etc) into an instr object and add into DB (look into config to see how this is done)
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());

        setResult(RESULT_OK, resultIntent); // TODO: write onActivityResult() method in AddNewInstrument.java, where this activity was called from... will have to do it in config2 too
        finish();
    }

}
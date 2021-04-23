
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

// This activity is accessed from the AddNewInstrument.java activity OR the EditInstrument.java activity
// ^ They both disallow changing instrument type of the string we're adding
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
    private Spinner spinnerStrInstrType;

    String instrBrandName;
    String instrModelName;
    boolean isAcoustic;
    String instrType;
    String iName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_string_from_add_new_instr);

        newStrBrandNamePrompt = (EditText) findViewById(R.id.newStrBrandName);
        newStrModelNamePrompt = (EditText) findViewById(R.id.newStrModelName);
        newStrCostPrompt = (EditText) findViewById(R.id.newStrCost);

        TextView instrTypePrompt = (TextView) findViewById(R.id.strInstrType);

        // get corresponding instrument info from intent
        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");

        instrBrandName = intent.getStringExtra("instrBrandName");
        instrModelName = intent.getStringExtra("instrModelName");
        isAcoustic = intent.getBooleanExtra("isAcoustic", false);
        instrType = intent.getStringExtra("instrType");
        iName = intent.getStringExtra("iName");

        // set the fields with info we just grabbed from intent
        instrTypePrompt.setText(instrType);
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

    public void addNewStr(View view) throws SQLException {
        String strBrandName = newStrBrandNamePrompt.getText().toString();
        String strModelName = newStrModelNamePrompt.getText().toString();
        //instrType = spinnerStrInstrType.getSelectedItem().toString();
        String strTension = spinnerStrTension.getSelectedItem().toString();
        float strCost;
        try {
            strCost = Float.parseFloat(newStrCostPrompt.getText().toString());
        } catch (NumberFormatException e) {
            strCost = Float.parseFloat("0");
        }
        S1.init(true);
        S1.setBrand(strBrandName);   // using a new StringSet obj S2 to not interfere with present strings selected
        S1.setModel(strModelName);
        S1.setType(instrType);
        S1.setTension(strTension);
        S1.setCost(strCost);
        S1.insertStrings(context);  // insert to DB
        A1.setStringsCnt(A1.getStringsCnt()+1);  // update appState
        System.out.println("DEBUG== Str to DB "+strBrandName+"-"+strModelName+" "+instrType+" "+strTension+" "+strCost+" ");

        // Since we use auto-generated StringsID in DB we need to get it from the last record written
        // and set in I1 before return to AddNewInstr
        ArrayList<String> alist = S1.getStringsStrList(context, instrType);
        String tmp = alist.get(alist.size()-1);
        System.out.println("##=== lastitem"+tmp);
        String token = tmp.split(":")[1];
        int newstrid = Integer.parseInt(token.split(" ")[0].trim());
        I1.setStringsID(newstrid);
        S1.setStringsID(newstrid);
        ///////////////////
        Intent resultIntent = new Intent();
        // TODO: Add instr info (brandName + modelName + instrType + cost + tension) into a str object and add into DB, linking it to the current instrument
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        // I pass the below info back and forth in case the user is still in the middle of filling out the form, I don't want their changes to be lost in the AddNewInstrument screen when they enter the AddNewString activity
        resultIntent.putExtra("instrBrandName", instrBrandName);
        resultIntent.putExtra("instrModelName", instrModelName);
        resultIntent.putExtra("isAcoustic", isAcoustic);
        resultIntent.putExtra("instrType", instrType);
        resultIntent.putExtra("iName", iName);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
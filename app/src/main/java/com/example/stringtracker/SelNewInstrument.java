package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SelNewInstrument extends AppCompatActivity {
/* Select New Instrument Only needs to prompt user to select a new instrument (no need for string)
Deleting a string needs us to select a new instrument (which assumes the last used string).
 */
    public static final int ADD_NEW_INSTR_REQUEST = 1;
    public static final int EDIT_INSTR_REQUEST = 2;

    private ArrayList<String> instrList;
    private Spinner spinner1;
    private ArrayAdapter<String> dataAdapter;
    private Button addNewInstrButton;
    private ArrayList<String> addedInstruments = new ArrayList<>();

    private String currInstName = "";
    private int currInstIndex;
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = SelNewInstrument.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("promptSelectNewInstr() proc'd successfully");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_new_instrument);
        Intent intent = getIntent();
        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        addItemsOnSpinner1();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void addItemsOnSpinner1() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        instrList = I1.getInstrStrList(context);
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
                // Log.d(LOG_TAG, "Add New Instrument Button clicked!"); // LOG_TAG is giving me errors??
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // New instrument added
        if (requestCode == ADD_NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                appState = data.getStringExtra("appstate");
                instState = data.getStringExtra("inststate");
                strState = data.getStringExtra("strstate");
                A1.setAppState(appState);  // Restore data object states on return
                I1.setInstState(instState);
                S1.setStrState(strState);
                dataAdapter.notifyDataSetChanged();
                spinner1.setSelection(0);  // TODO set selection to what was just added
            }
        }
    }

    public void confirmSelNewInstr(View view){
        currInstIndex = spinner1.getSelectedItemPosition();
        currInstName = instrList.get(currInstIndex);
        Intent resultIntent = new Intent(this, Configuration2.class);
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
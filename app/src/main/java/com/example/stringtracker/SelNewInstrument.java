package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

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

    private ArrayList<String> instrList;
    private Spinner spinner1;
    private ArrayAdapter<String> dataAdapter;
    private Button addNewInstrButton;

    private String currInstName = "";
    private int currInstIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_new_instrument);
        instrList = getIntent().getStringArrayListExtra("instrList");

        addItemsOnSpinner1();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

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
                // Log.d(LOG_TAG, "Add New Instrument Button clicked!"); // LOG_TAG is giving me errors??
                Intent i = new Intent(getApplicationContext(), AddNewInstrument.class);
                i.putExtra("fromActivity", "fromSelNewInstrument");
                startActivityForResult(i, ADD_NEW_INSTR_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // New instrument added
        if (requestCode == ADD_NEW_INSTR_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply =
                        data.getStringExtra(AddNewInstrument.instName);
                instrList.add(reply);
                dataAdapter.notifyDataSetChanged();
                Toast.makeText(SelNewInstrument.this, "Added new instrument \"" + reply + "\"", Toast.LENGTH_SHORT).show();
            }
        }

        // TODO: Add in instrument edited activityResult
    }

    public void launchEditInstrument(View view) {
    }
}
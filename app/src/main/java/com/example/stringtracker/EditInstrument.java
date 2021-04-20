package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditInstrument extends AppCompatActivity {
    public static final String newInstrName = "";
    private EditText instrName;

    EditText iBrand;
    EditText iModel;
    Button buttonRet;

    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Context context = EditInstrument.this;
    String appState;  // *** update local A1, I1, S1 objects to present state
    String instState;
    String strState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instrument);
        Intent intent = getIntent();

        appState = intent.getStringExtra("appstate");
        instState = intent.getStringExtra("inststate");
        strState = intent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);

        String name = intent.getStringExtra("iName");
        instrName = findViewById(R.id.editTextEditInstrName);
        instrName.setText(name, TextView.BufferType.EDITABLE);

        iBrand = (EditText) findViewById(R.id.editTextBrand2);
        iModel = (EditText) findViewById(R.id.editTextModel2);
        iBrand.setText(I1.getBrand());  // EXAMPLE loading data object values in editText
        iModel.setText(I1.getModel());

    }

    public void updateInstr(View view){
        I1.setBrand(iBrand.getText().toString());
        I1.setModel(iModel.getText().toString());
        I1.updateInstr(I1.getInstrID(), context);  // Updates DB settings
        String newName = instrName.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(newInstrName, "NormalReturn");
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void deleteInstr(View view){
        Intent resultIntent = new Intent();
        A1.setInstrumentCnt(A1.getInstrumentCnt()-1);  // need
        resultIntent.putExtra("appstate", A1.getAppState());
        resultIntent.putExtra("inststate", I1.getInstState());
        resultIntent.putExtra("strstate", S1.getStrState());
        resultIntent.putExtra(newInstrName, "000000000"); // bad coding practice, TODO fix later
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
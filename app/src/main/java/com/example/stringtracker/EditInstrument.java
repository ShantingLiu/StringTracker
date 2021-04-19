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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instrument);
        Intent intent = getIntent();

        String appState;  // *** update local A1, I1, S1 objects to present state
        String instState;
        String strState;
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


        // Return button - a good idea to keep this with state passing intact
        buttonRet = findViewById(R.id.buttonReturn2);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();

                // TODO - Add signalling parameters to intent for jenny's messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


    }

    public void updateInstr(View view){
        String newName = instrName.getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(newInstrName, newName);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void deleteInstr(View view){
        Intent replyIntent = new Intent();
        replyIntent.putExtra(newInstrName, "000000000"); // bad coding practice, TODO fix later
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
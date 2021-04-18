package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddNewInstrument extends AppCompatActivity {
    public static String instName = "";
    private EditText newInstrNamePrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        newInstrNamePrompt = findViewById(R.id.newInstrName);
        Intent intent = getIntent();        //replyTo = intent.getStringExtra("fromActivity");
    }

    // returns new instrument data back to activity it came from
    public void addNewInstr(View view){
        String instrName = newInstrNamePrompt.getText().toString();
        Intent entry = new Intent();
        entry.putExtra(instName, instrName);
        setResult(RESULT_OK, entry);
        finish();

    }

}
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddNewInstrument extends AppCompatActivity {
    private EditText newInstrNamePrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_instrument);
        newInstrNamePrompt = findViewById(R.id.newInstrName);
    }

    // returns new instrument data back to Configuration activity
    public void addNewInstr(View view){
        String instrName = newInstrNamePrompt.getText().toString();
        Intent entry = new Intent(AddNewInstrument.this, Configuration2.class);
        entry.putExtra("instName", instrName);
        startActivity(entry);
    }

}
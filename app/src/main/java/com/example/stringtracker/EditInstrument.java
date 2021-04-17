package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditInstrument extends AppCompatActivity {
    public static final String newInstrName = "";
    private EditText instrName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instrument);
        Intent intent = getIntent();
        String name = intent.getStringExtra("iName");
        instrName = findViewById(R.id.editTextEditInstrName);
        instrName.setText(name, TextView.BufferType.EDITABLE);
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
package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EditInstrument extends AppCompatActivity {
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
}
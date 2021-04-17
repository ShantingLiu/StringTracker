package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class SelNewInstrument extends AppCompatActivity {
/* Select New Instrument Only needs to prompt user to select a new instrument (no need for string)
Deleting a string needs us to select a new instrument (which assumes the last used string).
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_new_instrument);
    }

    public void launchEditInstrument(View view) {
    }
}
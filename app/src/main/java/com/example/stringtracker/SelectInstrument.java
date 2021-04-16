package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
// Select Instrument ListView Screen -wkd
public class SelectInstrument extends AppCompatActivity {

    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Button buttonRet;
    ArrayList<String> ilist = new ArrayList<String>();
    Context context = SelectInstrument.this;
    ListView lv_instruments;
    ArrayAdapter ad;
    TextView insSelTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_instrument);
        String appState;
        String instState;
        String strState;
        Intent mIntent = getIntent();
        appState = mIntent.getStringExtra("appstate");
        instState = mIntent.getStringExtra("inststate");
        strState = mIntent.getStringExtra("strstate");

        A1.setAppState(appState);  // init objects from intent
        I1.setInstState(instState);
        S1.setStrState(strState);
        // set display
        insSelTV = (TextView) findViewById(R.id.strSelTV);
        String selInstText = "Selected Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel();
        insSelTV.setText(selInstText);
        insSelTV.setVisibility(View.VISIBLE);


        //A1.loadRunState();
        lv_instruments  = findViewById(R.id.lv_instruments);
        ilist = I1.getInstrStrList(context);
        ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ilist);
        lv_instruments.setAdapter(ad);

        lv_instruments.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectInstrument.this, "pos="+position+" name="+ilist.get(position), Toast.LENGTH_SHORT).show();
                String tmp = ilist.get(position);
                String token = tmp.split(":")[1];
                int newinstrid = Integer.parseInt(token.split(" ")[0].trim());

                // set new instrumentID load new Instrument and StringSet from DB
                A1.setInstrID(newinstrid);
                I1.loadInstr(newinstrid, context);
                S1.loadStrings(I1.getStringsID(), context);
                A1.saveRunState();  // be sure run state changes saved
                // update display
                String selInstText = "Selected Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel();
                insSelTV.setText(selInstText);
                insSelTV.setVisibility(View.VISIBLE);
            }
         });

        buttonRet = findViewById(R.id.buttonRet5);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //A1.setInstrumentID(888);
                //A1.saveRunState();  // DEBUG using stored data file for messaging
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
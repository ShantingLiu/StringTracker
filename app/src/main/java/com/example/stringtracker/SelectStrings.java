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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SelectStrings extends AppCompatActivity {
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    Button buttonRet;
    ArrayList<String> slist = new ArrayList<String>();
    Context context = SelectStrings.this;
    ListView lv_strings;
    ArrayAdapter ad;
    TextView strSelTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_strings);

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
        strSelTV = (TextView) findViewById(R.id.strSelTV);
        String selInstText = "Selected Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel()
                +"\n           Strings ID:"+S1.getStringsID()+" "+S1.getBrand()+"-"+S1.getModel()+"("+S1.getTension()+")";
        strSelTV.setText(selInstText);
        strSelTV.setVisibility(View.VISIBLE);

        //A1.loadRunState();
        lv_strings  = findViewById(R.id.lv_strings);
        try {
            slist = S1.getStringsStrList(context, I1.getType());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, slist);
        lv_strings.setAdapter(ad);

        lv_strings.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectStrings.this, "pos="+position+" name="+slist.get(position), Toast.LENGTH_SHORT).show();
                String tmp = slist.get(position);
                String token = tmp.split(":")[1];
                int newstrid = Integer.parseInt(token.split(" ")[0].trim());

                /// String Change Even Sequence ////
                I1.logStringChange();
                // do not attempt to update if there are no sessions
                if (I1.getPlayTime() > 0  && I1.getSessionCnt() > 0) {

                    S1.updateAvgSent(I1.getSentLog(), I1.getPlayTime());

                    if (!A1.getTestMode()) {  // if in normal operating mode clear sent log
                        try {
                            I1.clearSentLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // set new instrumentID load new Instrument and StringSet from DB
                I1.setStringsID(newstrid);
                S1.loadStrings(I1.getStringsID(), context);
                I1.init();   // clear for new string cycle
                I1.updateInstr(I1.getInstrID(), context);  // be sure to update DB item for new strings selected
                A1.init();  // clear internal time values

                A1.setInstState(I1.getInstState());  // update object state strings in AppState
                A1.setStrState(S1.getStrState());
                A1.saveRunState();  // be sure run state changes saved

                String selInstText = "Selected Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel()
                        +"\n           Strings ID:"+S1.getStringsID()+" "+S1.getBrand()+"-"+S1.getModel()+"("+S1.getTension()+")";
                strSelTV.setText(selInstText);
                strSelTV.setVisibility(View.VISIBLE);
            }
        });

        buttonRet = findViewById(R.id.buttonRet5);
        buttonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
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
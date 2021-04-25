package com.example.stringtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;

public class IntonationActivityGraph extends AppCompatActivity {
    LineChart lineChart;
    LineDataSet stringIntonationDataSet1;
    LineDataSet stringIntonationDataSet2;

    Button buttonRet;

    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();
    Instrument I1 = new Instrument();
    StringSet sA = new StringSet();
    StringSet sB = new StringSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intonation_comparison_graph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.lineChartIntonation);
        String appState;
        String instState;
        String strState;
        Intent mIntent = getIntent();      // passing stringset object states into compare activity
        appState = mIntent.getStringExtra("appstate");
        instState = mIntent.getStringExtra("inststate");
        strState = mIntent.getStringExtra("strstate");
        A1.setAppState(appState);
        I1.setInstState(instState);
        S1.setStrState(strState);
        String strState1;
        String strState2;
        strState1 = mIntent.getStringExtra("sA");
        strState2 = mIntent.getStringExtra("sB");

        sA.setStrState(strState1);
        sB.setStrState(strState2);


        stringIntonationDataSet1 = new LineDataSet(loadStringIntonation(sA), "String 1 Intonation");
        stringIntonationDataSet1.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);


        stringIntonationDataSet2 = new LineDataSet(loadStringIntonation(sB), "String 2 Intonation");
        stringIntonationDataSet2.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(stringIntonationDataSet1);
        dataSets.add(stringIntonationDataSet2);


        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();



        buttonRet = findViewById(R.id.buttonRet8);
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
    }//end of oncreate

    public ArrayList<Entry> loadStringIntonation(StringSet x){
        //projection
        float projection[] = x.getAvgInton();
        ArrayList<Entry> projectionData = new ArrayList<Entry>();
        for(int i = 0; i < projection.length; i++){
            projectionData.add(new Entry(projection[i], i));
        }
        Collections.sort(projectionData, new EntryXComparator());
        return projectionData;
    }
}
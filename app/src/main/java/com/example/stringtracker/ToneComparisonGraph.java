package com.example.stringtracker;

import android.content.Intent;
import android.os.Bundle;

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

public class ToneComparisonGraph extends AppCompatActivity {
    LineChart lineChart;
    LineDataSet stringToneDataSet1;
    LineDataSet stringToneDataSet2;

    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();
    Instrument I1 = new Instrument();
    StringSet sA = new StringSet();
    StringSet sB = new StringSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_comparison_graph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.lineChartTone);
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


        stringToneDataSet1 = new LineDataSet(loadStringTone(sA), "String 1 Tone");
        stringToneDataSet1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        stringToneDataSet1.setLineWidth(5);


        stringToneDataSet2 = new LineDataSet(loadStringTone(sB), "String 2 Tone");
        stringToneDataSet2.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        stringToneDataSet2.setLineWidth(5);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(stringToneDataSet1);
        dataSets.add(stringToneDataSet2);


        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.animateY(3000);
        lineChart.invalidate();




    }

    public ArrayList<Entry> loadStringTone(StringSet x){
        //Tone
        float tone[] = x.getAvgTone();
        ArrayList<Entry> toneData = new ArrayList<Entry>();
        for(int i = 0; i < tone.length; i++){
            toneData.add(new Entry(i, tone[i]));
        }
        Collections.sort(toneData, new EntryXComparator());
        return toneData;
    }
}
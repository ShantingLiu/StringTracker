package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;

public class LineGraphActivity extends AppCompatActivity  {

//    Context context = LineGraphActivity.this;
    LineChart lineChart;
    LineDataSet projectionDataSet;
    LineDataSet intonationDataSet;
    LineDataSet toneDataSet;

    Button buttonRet;

    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    Instrument I1 = new Instrument();
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        lineChart = findViewById(R.id.lineChart);

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

        // back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load projection Data into LineDataSet
       projectionDataSet = new LineDataSet(stringProjections(), "projection");
       projectionDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);

        //load Intonation Data into LineDataSet
       intonationDataSet = new LineDataSet(stringIntonation(), "Intonation");
       intonationDataSet.setColor(ColorTemplate.COLORFUL_COLORS[3]);

        //load Tone Data into LineDataSet
       toneDataSet = new LineDataSet(stringTone(), "Tone");
       toneDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);

//        System.out.println("aman");


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(projectionDataSet);
        dataSets.add(intonationDataSet);
        dataSets.add(toneDataSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();

        buttonRet = findViewById(R.id.buttonRet6);
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

    public ArrayList<Entry>stringProjections(){
        //projection
       float projection[] = S1.getAvgProj();
        ArrayList<Entry> projectionData = new ArrayList<Entry>();
        for(int i = 0; i < projection.length; i++){
            projectionData.add(new Entry(projection[i], i));
        }
        Collections.sort(projectionData, new EntryXComparator());
        return projectionData;
    }

    public ArrayList<Entry>stringIntonation() {
        float intonation[] = S1.getAvgInton();
        ArrayList<Entry> intonationData = new ArrayList<>();
        for(int i = 0; i < intonation.length; i++){
            intonationData.add(new Entry(intonation[i], i));
        }
        Collections.sort(intonationData, new EntryXComparator());
        return intonationData;
    }
    public ArrayList<Entry>stringTone() {
        float tone[] = S1.getAvgTone();
        ArrayList<Entry> toneData = new ArrayList<>();
        for(int i = 0; i < tone.length; i++){
            toneData.add(new Entry(tone[i], i));
        }
        Collections.sort(toneData, new EntryXComparator());
        return toneData;
    }

}
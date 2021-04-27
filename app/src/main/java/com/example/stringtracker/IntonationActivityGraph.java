package com.example.stringtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    Button buttonIntonRet;

    LineChart lineChart;
    LineDataSet stringIntonationDataSet1;
    LineDataSet stringIntonationDataSet2;

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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        String string1 = sA.getBrand() +" Model: " + sA.getModel();
        String string2 = sB.getBrand() +" Model: " + sB.getModel();

        stringIntonationDataSet1 = new LineDataSet(loadStringIntonation(sA), string1);
        stringIntonationDataSet1.setColor(Color.RED);
        stringIntonationDataSet1.setLineWidth(5);

        stringIntonationDataSet2 = new LineDataSet(loadStringIntonation(sB), string2);
        stringIntonationDataSet2.setColor(Color.BLUE);
        stringIntonationDataSet2.setLineWidth(5);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(stringIntonationDataSet1);
        dataSets.add(stringIntonationDataSet2);


        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        int color = ContextCompat.getColor(IntonationActivityGraph.this, R.color.lifecolor0); //Background Color
        lineChart.setBackgroundColor(color);
        lineChart.animateY(3000);
        lineChart.invalidate();

        //RETURN BUTTON

        buttonIntonRet = findViewById(R.id.buttonIntonRet);
        buttonIntonRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("appstate", A1.getAppState());
                resultIntent.putExtra("inststate", I1.getInstState());
                resultIntent.putExtra("strstate", S1.getStrState());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });



    }

    public ArrayList<Entry> loadStringIntonation(StringSet x){
        //projection
        float projection[] = x.getAvgInton();
        ArrayList<Entry> projectionData = new ArrayList<Entry>();
        for(int i = 0; i < projection.length; i++){
            projectionData.add(new Entry(i, projection[i]));
        }
        Collections.sort(projectionData, new EntryXComparator());
        return projectionData;
    }
}
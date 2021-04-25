package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

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

public class ProjectionComparisonGraph extends AppCompatActivity {

    LineChart lineChart;
    LineDataSet stringProjectionDataSet1;
    LineDataSet stringProjectionDataSet2;

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
        setContentView(R.layout.activity_projection_comparison_graph2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.lineChartProjection);
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
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        LineData chartData = new LineData();

        stringProjectionDataSet1 = new LineDataSet(loadStringProjection1(sA), "String 1 ProJ");
        stringProjectionDataSet1.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);


        stringProjectionDataSet2 = new LineDataSet(loadStringProjection1(sB), "String 2 ProJ");
        stringProjectionDataSet2.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(stringProjectionDataSet1);
        dataSets.add(stringProjectionDataSet2);

        chartData.addDataSet(stringProjectionDataSet1);
        chartData.addDataSet(stringProjectionDataSet2);

        chartData = new LineData(dataSets);

        //LineData data = new LineData(dataSets);//what ever is set here is plotted
        //lineChart.setData(data);

        lineChart.setData(chartData);
        lineChart.invalidate();

        buttonRet = findViewById(R.id.buttonRet7);
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

    public ArrayList<Entry> loadStringProjection1( StringSet x){ //passes sA or sB in method
        //projection
        float projection[] = x.getAvgProj();
        ArrayList<Entry> projectionData = new ArrayList<Entry>();
        for(int i = 0; i < projection.length; i++){
            projectionData.add(new Entry(projection[i], i));
        }
        Collections.sort(projectionData, new EntryXComparator());
        return projectionData;
    }
//    public ArrayList<Entry> loadStringProjection2(){
//        //projection
//        float projection2[] = sB.getAvgProj();
//        ArrayList<Entry> projectionData2 = new ArrayList<Entry>();
//        for(int i = 0; i < projection2.length; i++){
//            projectionData2.add(new Entry(projection2[i], i));
//        }
//        Collections.sort(projectionData2, new EntryXComparator());
//        return projectionData2;
//    }
}
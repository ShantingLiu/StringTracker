package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class LineGraphActivity extends AppCompatActivity  {


    LineChart linechart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        linechart = (LineChart) findViewById(R.id.lineChart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        linechart.setOnChartGestureListener(LineGraphActivity.this);
//        linechart.setOnChartValueSelectedListener(LineGraphActivity.this);

        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(false);


        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(6, 80f));
        yValues.add(new Entry(9, 90f));
        yValues.add(new Entry(3, 40f));
        yValues.add(new Entry(2, 55f));

        LineDataSet set1 = new LineDataSet(yValues, "Tonality");

        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        linechart.setData(data);


    }
}
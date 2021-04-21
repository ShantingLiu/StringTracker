package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class CompareStrings2 extends AppCompatActivity {

    LineChart Linegraph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_strings2);
   // in this example, a LineChart is initialized from xml
        Linegraph = (LineChart) findViewById(R.id.linegraph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Linegraph.setDragEnabled(true);
        Linegraph.setScaleEnabled(false);

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

        Linegraph.setData(data);


    }
    }

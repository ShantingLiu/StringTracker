package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieActivity extends AppCompatActivity {


    PieChart pie_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pie_chart = findViewById(R.id.pie_chart);

        ArrayList<PieEntry> myPieData = new ArrayList<>();

        myPieData.add(new PieEntry(1,"A"));
        myPieData.add(new PieEntry(2,"B"));
        myPieData.add(new PieEntry(3,"C"));
        myPieData.add(new PieEntry(4,"D"));

        PieDataSet pieDataSet = new PieDataSet(myPieData, "Intonation");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(25);


        PieData pieData = new PieData(pieDataSet);
        pie_chart.setData(pieData);
        pie_chart.setBackgroundColor(Color.LTGRAY);

        pie_chart.setDrawSlicesUnderHole(false);
        pie_chart.setDrawHoleEnabled(false);
        pie_chart.getDescription().setEnabled(false);
        pie_chart.animateXY(2000,2000);
    }
}
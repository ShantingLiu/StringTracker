package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarActivity extends AppCompatActivity {


    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        barChart=findViewById(R.id.barChart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<BarEntry> myBarList = new ArrayList<>();

        myBarList.add(new BarEntry(4, 5));
        myBarList.add(new BarEntry(6, 9));
        myBarList.add(new BarEntry(2, 8));
        myBarList.add(new BarEntry(3, 7));
        myBarList.add(new BarEntry(5, 6));


        BarDataSet barDataSet = new BarDataSet(myBarList, "Tonality");

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.LTGRAY);
        barChart.animateY(3000);





    }
}
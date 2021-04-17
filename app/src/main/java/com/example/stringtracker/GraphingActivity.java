package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GraphingActivity extends AppCompatActivity {

    Button pieChart, barChart, lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



       pieChart = findViewById(R.id.pie_btn);
       barChart = findViewById(R.id.bar_btn);
       lineChart = findViewById(R.id.line_btn);

        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GraphingActivity.this,PieActivity.class));
            }
        });

        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GraphingActivity.this, BarActivity.class));
            }
        });

        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GraphingActivity.this, LineGraphActivity.class));
            }
        });


    }
}
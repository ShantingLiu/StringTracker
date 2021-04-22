package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
//hi
public class CompareStrings2 extends AppCompatActivity {
    // local copies of the main stringset, instrument, and appstate objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();   // 2nd stringset for comparison
    Instrument I1 = new Instrument();

    LineChart Linegraph;
    Button buttonRet;
    private Spinner spinner1, spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_strings2);

        //dbstuff i believe
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

   // in this example, a LineChart is initialized from xml
        Linegraph = (LineChart) findViewById(R.id.linegraph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Linegraph.setDragEnabled(true);
        Linegraph.setScaleEnabled(false);
        LineData chartData = new LineData();

        //LineDataSet set1 = new LineDataSet(string1,"hi");
        //chartData.addDataSet(set1);

       // LineDataSet set2 = new LineDataSet(...);
       // chartData.addDataSet(set2);

        //List<Entry> string1 = new ArrayList<Entry>();
       // List<Entry> string2 = new ArrayList<Entry>();


       ArrayList<Entry> string1 = new ArrayList<>();
        string1.add(new Entry(0, 60f));
        string1.add(new Entry(1, 50f));
        string1.add(new Entry(6, 80f));
        string1.add(new Entry(9, 90f));
        string1.add(new Entry(3, 40f));
        string1.add(new Entry(2, 55f));

        ArrayList<Entry> string2 = new ArrayList<>();
        string2.add(new Entry(0, 90f));
        string2.add(new Entry(1, 50f));
        string2.add(new Entry(6, 70f));
        string2.add(new Entry(9, 80f));
        string2.add(new Entry(3, 60f));
        string2.add(new Entry(2, 20f));

        LineDataSet set1 = new LineDataSet(string1, "string1");
        chartData.addDataSet(set1);

        LineDataSet set2 = new LineDataSet(string2, "string2");
        chartData.addDataSet(set2);

        Linegraph.setData(chartData);
        Linegraph.invalidate();
        //set1.setFillAlpha(110);

        //could would messup two lines
      /*  set1.setFillAlpha(110);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        dataSets.add(set2);

        LineData data = new LineData(dataSets);
        LineData data2 = new LineData(dataSets2);

        Linegraph.setData(data);
        Linegraph.setData(data2);

        */


        // took return button method to go back to screen with out it can leave page
        buttonRet = findViewById(R.id.returnline);
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

      //testing spinnner method
        //addItemsOnSpinner1();
        //addListenerOnButton();
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("string 1");
        list.add("string 2");
        list.add("string 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }//oncreate

    //adding itm dynmically
    /*public void addItemsOnSpinner1() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("string 1");
        list.add("string 2");
        list.add("string 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        //spinner2 = (Spinner) findViewById(R.id.spinner2);

    }

     */



}

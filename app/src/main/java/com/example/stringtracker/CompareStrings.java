package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class CompareStrings extends AppCompatActivity {

    // local copies of the main stringset, instrument, and appstate objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();   // 2nd stringset for comparison
    Instrument I1 = new Instrument();


    StringSet Sb = new StringSet();


    Button buttonRet, buttonProjection, buttonTone, buttonIntonation;
    Context context = CompareStrings.this;

    //Text Views


    TextView stringLabelTV1;
    TextView stringStatTV1;
    String strStats1;
    String selectedString1 = "String Set 1";

    TextView stringLabelTV2;
    TextView stringStatTV2;
    String strStats2;
    String selectedString2 = "String Set 2";





    private final String[] instrTypes = new String[]{"Cello", "Bass", "Banjo", "Guitar", "Mandolin", "Viola", "Violin", "Other"};
    ArrayList<String> slist = new ArrayList<String>();
    private ArrayList<String> stringsList = new ArrayList<>();
    Spinner spinnerString1, spinnerString2, spinnerInstrTypes;
    private ArrayAdapter<String> dataAdapter1;
    private ArrayAdapter<String> dataAdapter2;
    private ArrayAdapter<String> dataAdapterInstr;

    String instrType;
    String strGraphName1 = "none";
    String strGraphName2 = "none";
    float [] strData1;
    float [] strData2;

    private boolean userIsInteracting = false;
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_strings);


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
        int strId = S1.getStringsID();  // get current string selection for starting value

        //set textViews
        stringLabelTV1 = (TextView) findViewById(R.id.stringLableTV1);
        stringLabelTV2 = (TextView) findViewById(R.id.stringLableTV2);
        stringStatTV1 = (TextView) findViewById(R.id.stringStatsTV1);
        stringStatTV2 = (TextView) findViewById(R.id.stringStatsTV2);



        StringSet sA = new StringSet();
        StringSet sB = new StringSet();

        instrType = I1.getType();

        // Populate initial strings list
        try {
            populateList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // set up instrType spinner
        addItemsOnSpinnerInst();
        addListenerOnSpinnerItemSelection();
        spinnerInstrTypes.setSelection(dataAdapterInstr.getPosition(instrType));

        // setup strings1 spinner
        addItemsOnSpinner1();
        addListenerOnSpinnerItemSelection();
        spinnerString1.setSelection(findPosition(slist, strId));

        // setup strings2 spinner
        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();
        spinnerString2.setSelection(findPosition(slist, strId));

        spinnerInstrTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // create a new adapter with the corresponding values

                instrType = spinnerInstrTypes.getItemAtPosition(spinnerInstrTypes.getSelectedItemPosition()).toString();
                System.out.println("instrType="+instrType);

                // Re-Populate strings list with selected instrType
                try {
                    populateList();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                addItemsOnSpinner1();
                addItemsOnSpinner2();

                // setup compare strings data objects instrType
                sA.setType(instrType);
                sB.setType(instrType);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // nothing selected, so set empty options
            }

        });

        //end of ON SElect

        spinnerString1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp = slist.get(position);
                // parse and set StringsID for data obj
                String token = tmp.split(":")[1];
                int newstrid = Integer.parseInt(token.split(" ")[0].trim());
                // load selected string data into obj
                sA.loadStrings(newstrid, context);


                //load String stats

                stringLabelTV1.setText(selectedString1);
                stringLabelTV1.setVisibility(View.VISIBLE);

                String strStats1 = "Brand: " + sA.getBrand() + "\n" + "Model: " + sA.getModel() + "\n"+ "Avg Life: " + sA.getAvgLife()+ "\n" + "Cost: " + sA.getCost();
                stringStatTV1.setText(strStats1);
                stringStatTV1.setVisibility(View.VISIBLE);



                strGraphName1 = sA.getBrand()+"-"+sA.getModel(); // create label for graph item 1
                System.out.println("strGraphName1="+strGraphName1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //  empty
            }
        });

        spinnerString2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp = slist.get(position);
                // parse and set StringsID for data obj
                String token = tmp.split(":")[1];
                int newstrid = Integer.parseInt(token.split(" ")[0].trim());
                // load selected string data into obj
                sB.loadStrings(newstrid, context);

                stringLabelTV2.setText(selectedString2);
                stringLabelTV2.setVisibility(View.VISIBLE);
                String strStats2 = "Brand: " + sB.getBrand() + "\n" + "Model: " + sB.getModel() + "\n" + "Avg Life: " + sB.getAvgLife() + "\n" + "Cost: " + sB.getCost();
                stringStatTV2.setText(strStats2);
                stringStatTV2.setVisibility(View.VISIBLE);


                strGraphName2 = sB.getBrand()+"-"+sB.getModel();  // create label for graph item 2
                System.out.println("strGraphName2="+strGraphName2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
            }
        });


        buttonProjection = findViewById(R.id.buttonProjection);
        buttonProjection.setVisibility(View.VISIBLE);
        buttonProjection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CompareStrings.this, ProjectionComparisonGraph.class);
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                String strStateSa = sA.getStrState();
                String strStateSb = sB.getStrState();
//                strData1 = sA.getAvgProj();
//                strData2 = sB.getAvgProj();
                intent.putExtra("sA", strStateSa);
                intent.putExtra("sB", strStateSb);
                startActivity(intent);






//                FragmentManager fmanager = getSupportFragmentManager();
//                SentGraph SentDialog = new SentGraph();
//                SentDialog.show(fmanager, "Graph Sentiment");
//                fmanager.beginTransaction().replace(R.id.container,SentDialog).commit();
                /*Intent intent = new Intent();
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                startActivity(intent);*/
            }
        });

        buttonIntonation = findViewById(R.id.buttonIntonation);
        buttonIntonation.setVisibility(View.VISIBLE);
        buttonIntonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CompareStrings.this, IntonationActivityGraph.class);
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                String strStateSa = sA.getStrState();
                String strStateSb = sB.getStrState();
                intent.putExtra("sA", strStateSa);
                intent.putExtra("sB", strStateSb);
                startActivity(intent);

//                FragmentManager fmanager = getSupportFragmentManager();
//                SentGraph SentDialog = new SentGraph();
//                SentDialog.show(fmanager, "Graph Sentiment");
//                fmanager.beginTransaction().replace(R.id.container,SentDialog).commit();
                /*Intent intent = new Intent();
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                startActivity(intent);*/
            }
        });

        buttonTone = findViewById(R.id.buttonTone);
        buttonTone.setVisibility(View.VISIBLE);
        buttonTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CompareStrings.this, IntonationActivityGraph.class);
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                String strStateSa = sA.getStrState();
                String strStateSb = sB.getStrState();
                intent.putExtra("sA", strStateSa);
                intent.putExtra("sB", strStateSb);
                startActivity(intent);

//                FragmentManager fmanager = getSupportFragmentManager();
//                SentGraph SentDialog = new SentGraph();
//                SentDialog.show(fmanager, "Graph Sentiment");
//                fmanager.beginTransaction().replace(R.id.container,SentDialog).commit();
                /*Intent intent = new Intent();
                String appState = A1.getAppState();
                String instState = I1.getInstState();
                String strState = S1.getStrState();
                intent.putExtra("appstate", appState);   // forward object states
                intent.putExtra("inststate", instState);
                intent.putExtra("strstate", strState);
                startActivity(intent);*/
            }
        });





//           In you graph buttons  Graph Projection





        buttonRet = findViewById(R.id.buttonRet3);
        buttonRet.setOnClickListener(new View.OnClickListener() {
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

    } ////// End OnCreate

    private int findPosition(ArrayList <String> x, int id){
        if (x == null){
            return 0;
        }
        int pos = 0;
        String s;
        for(int i = 0; i < x.size(); ++i){
            s = x.get(i);
            String token = s.split(":")[1];
            int tmpid = Integer.parseInt(token.split(" ")[0].trim());
            if(id == tmpid){
                pos = i;
            }
        }
        return pos;
    }

    public void populateList() throws SQLException {
        Toast.makeText(this, "onCreate() populating instrList", Toast.LENGTH_SHORT).show();
        slist.clear();
        slist = S1.getStringsStrList(context, instrType);  // *** gets Strings ArrayList for DB based on the Type of instrument selected in I1
    }

    public void addItemsOnSpinnerInst() {
        spinnerInstrTypes = (Spinner) findViewById(R.id.spinnerInstrumentTypes);
        dataAdapterInstr = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrTypes);
        dataAdapterInstr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrTypes.setAdapter(dataAdapterInstr);
    }

    public void addItemsOnSpinner1() {
        spinnerString1 = (Spinner) findViewById(R.id.spinnerString1);
        dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slist);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerString1.setAdapter(dataAdapter1);
    }
    public void addItemsOnSpinner2() {
        spinnerString2 = (Spinner) findViewById(R.id.spinnerString2);
        dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slist);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerString2.setAdapter(dataAdapter2);
    }

    public void addListenerOnSpinnerItemSelection(){ // TODO: DEBUG - This method is breaking the app
        spinnerInstrTypes = (Spinner) findViewById(R.id.spinnerInstrumentTypes);
        spinnerInstrTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "Saved New Instrument Selection: " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public void updateStatsDisplay(){
        int pctLife = 0;
        float costPerHr = 0.0f;
        float costPerHrExp = 0.0f;
        if(I1.getSessionCnt()>0){
            costPerHr = (S1.getCost()/(float)((float)I1.getPlayTime()/60.0f));
            if(S1.getAvgLife()>0){
                pctLife = 100 - (int)(100.0*(float)I1.getPlayTime()/(float)S1.getAvgLife());
                costPerHrExp = (S1.getCost()/(float)((float)S1.getAvgLife()/60.0f));
            }
        }

//        String selInstr =  "Instrument ID:"+I1.getInstrID()+" "+I1.getBrand()+"-"+I1.getModel()+" ("+I1.getType()+")";
//        String selStrings =  "String Set ID:"+S1.getStringsID()+" "+S1.getBrand()+"-"+S1.getModel()+" ("+S1.getType()+") \n"
//                +"Last Changed: "+I1.getChangeTimeStamp().split(" ")[0];
//        String strStats1 =  "Avg Life:"+S1.getAvgLife()+"min  Time played:"+I1.getPlayTime()+"min  Life remaining:"+pctLife+"%";
//        String strStats2 =  "Cost/hr(current):$"+String.format("%.2f", costPerHr)+"/hr   Cost/hr(expected):$"+String.format("%.2f",costPerHrExp)+"/hr";




        stringLabelTV2.setVisibility(View.VISIBLE);
        stringStatTV1.setVisibility(View.VISIBLE);
        stringStatTV2.setVisibility(View.VISIBLE);
    }

}
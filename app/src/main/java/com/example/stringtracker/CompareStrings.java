package com.example.stringtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompareStrings extends AppCompatActivity {

    // local copies of the main stringset, instrument, and appstate objects
    AppState A1 = new AppState();
    StringSet S1 = new StringSet();
    StringSet S2 = new StringSet();   // 2nd stringset for comparison
    Instrument I1 = new Instrument();

    StringSet Sa = new StringSet();
    StringSet Sb = new StringSet();
    Instrument Ia = new Instrument();

    Button buttonRet, projection, tone;
    Context context = CompareStrings.this;

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

            } // I may have to put something similar to this code on the onActivityResult() from the addString screen
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
                strGraphName1 = sA.getBrand()+"-"+sA.getModel(); // create label for graph item 1
                System.out.println("strGraphName1="+strGraphName1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
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
                strGraphName2 = sB.getBrand()+"-"+sB.getModel();  // create label for graph item 2
                System.out.println("strGraphName2="+strGraphName2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
            }
        });

        /*
           In you graph buttons  Graph Projection
           strData1 = sA.getAvgProj();
           strData2 = sB.getAvgProj();



        */
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

}
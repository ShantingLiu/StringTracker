package com.example.stringtracker_test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebugDataGen {

    DebugDataGen(){

    }

    ////////  Method to generate a list of random Instrument objects ///////////
    public ArrayList<Instrument> genInstList(){
        ArrayList <Instrument> ilist = new ArrayList<Instrument>();

        int n = 8;  // set number of instruments to generate here
        for(int i = 0; i < n; ++i){
            ilist.add(genInst());
        }
        return ilist;
    }
    /////  generates one random instrument /////
    public Instrument genInst(){
        Instrument I = new Instrument();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand [] = {"GHS", "D'Addario", "Martin", "Elixir", "Ernie Bal"};
        String sModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};
        String sTension [] = {"XL", "Light", "Medium", "Heavy"};
        String sType [] = {"guitar", "banjo", "mandolin", "violin", "cello"};

        String iBrand [] = {"Gibson", "Collings", "Fender", "Taylor", "PRS"};
        String iModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};

        int rand_sBr = rand.nextInt(5);
        int rand_sMo = rand.nextInt(5);
        int rand_sTe = rand.nextInt(4);
        int rand_sTy = rand.nextInt(5);
        int rand_sID = rand.nextInt(20) + 10;

        int rand_iBr = rand.nextInt(5);
        int rand_iMo = rand.nextInt(5);
        int rand_iID = rand.nextInt(20) + 100;

        I.setInstrID(rand_iID);
        I.setBrand(iBrand[rand_iBr]);
        I.setModel(iModel[rand_iMo]);
        I.setType(sType[rand_sTy]);
        I.setStringsID(rand_sID);  // match up strings with instrument
        return I;
    }

    ////////  Method to generate a list of random StringSet objects ///////////
    public ArrayList <StringSet> genStringsList(){
        ArrayList <StringSet> ilist = new ArrayList<StringSet>();

        int n = 8;  // set number of instruments to generate here
        for(int i = 0; i < n; ++i){
            ilist.add(genStrings());
        }
        return ilist;
    }
    /////  generates one random instrument /////
    public StringSet genStrings(){
        StringSet S = new StringSet();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        String sBrand [] = {"GHS", "D'Addario", "Martin", "Elixir", "Ernie Bal"};
        String sModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};
        String sTension [] = {"XL", "Light", "Medium", "Heavy"};
        String sType [] = {"guitar", "banjo", "mandolin", "violin", "cello"};

        String iBrand [] = {"Gibson", "Collings", "Fender", "Taylor", "PRS"};
        String iModel [] = {"A-180", "G-42", "Bronze", "ToneKing", "Slinky"};

        int rand_sBr = rand.nextInt(5);
        int rand_sMo = rand.nextInt(5);
        int rand_sTe = rand.nextInt(4);
        int rand_sTy = rand.nextInt(5);
        int rand_sID = rand.nextInt(20) + 10;

        int rand_iBr = rand.nextInt(5);
        int rand_iMo = rand.nextInt(5);
        int rand_iID = rand.nextInt(20) + 100;

        S.setStringsID(rand_sID);
        S.setBrand(sBrand[rand_sBr]);

        S.setModel(sModel[rand_sMo]);
        S.setTension(sTension[rand_sTe]);
        S.setType(sType[rand_sTy]);

        return S;
    }
    ////////////////////////////////////////////////////////////////////////////////


}

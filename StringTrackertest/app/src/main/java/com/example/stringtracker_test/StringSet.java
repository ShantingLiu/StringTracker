package com.example.stringtracker_test;

import android.util.Log;

import java.util.Arrays;

// StringSet Data Class with supporting functions   WKD 3-4-21
import java.util.List;

public class StringSet {
    private int StringsID;
    private String Brand;
    private String Model;
    private String Tension;
    private String Type;
    private Float Cost;
    private int AvgLife;
    private int ChangeCnt;
    private boolean FirstSession;  // flag for detecting first session set true for new strings
    private String AvgProjStr;    // strings to hold finite list of array data for DB storage
    private String AvgToneStr;
    private String AvgIntonStr;

    final int INTERVALS = 10;  // (constant) number of percent life per intervals
    private final String DELIM = "; ";  // delimiter for state passing data

    private float [] AvgProj = new float [INTERVALS];   // holds avg sentiment floats internal
    private float [] AvgTone = new float [INTERVALS];   // processing
    private float [] AvgInton = new float [INTERVALS];
    private float [] SessProj = new float [INTERVALS];   // holds current session sentiment float vals
    private float [] SessTone = new float [INTERVALS];   // for internal processing
    private float [] SessInton = new float [INTERVALS];

    // Constructors
    StringSet(StringSet x) {
        StringsID = x.getStringsID();
        Brand = x.getBrand();
        Model = x.getModel();
        Tension = x.getTension();
        Type = x.getType();
        Cost = x.getCost();
        AvgLife = x.getAvgLife();
        ChangeCnt = x.getChangeCnt();
        FirstSession = x.getFirstSession();
        AvgProjStr = x.getAvgProjStr();
        AvgToneStr = x.getAvgToneStr();
        AvgIntonStr = x.getAvgIntonStr();
    }
    // Constructors
    StringSet(int stid, String bra, String mod, String tens, String typ, float cost, int avglif,
              int chcnt, boolean firstses, String avgproj, String avgtone, String avginton) {
        StringsID = stid;
        Brand = bra;
        Model = mod;
        Tension = tens;
        Type = typ;
        Cost = cost;
        AvgLife = avglif;
        ChangeCnt = chcnt;
        FirstSession = firstses;
        AvgProjStr = avgproj;
        AvgToneStr = avgtone;
        AvgIntonStr = avginton;
    }
    // default values constructor
    StringSet() {
        StringsID = 0;
        Brand = "";
        Model = "";
        Tension = "";
        Type = "";
        Cost = 0.0f;
        AvgLife = 0;
        ChangeCnt = 0;
        FirstSession = true;
        // Build default avg sent strings with zeros of length INTERVALS+1
        StringBuilder defSent = new StringBuilder();
        for(int i = 0; i<INTERVALS-1;++i) {
            defSent.append("0, ");   // sentiment values w/ comma delimiters
        }
        defSent.append("0");  // add the last 0 without comma
        AvgProjStr = defSent.toString(); // strings hold list of text
        AvgToneStr = defSent.toString();
        AvgIntonStr = defSent.toString();
    }

    // Method to reset flag on string change
    void init() {
        FirstSession = true;  // reset flag
    }

    // Method to reset all values - dangerous
    void reset() {
        FirstSession = true;  // reset flag
        Cost = 0.0f;
        AvgLife = 0;
        ChangeCnt = 0;
        StringsID = 0;
        Brand = "";
        Model = "";
        Tension = "";
        Type = "";

        // Build default avg sent strings with zeros of length INTERVALS+1
        StringBuilder defSent = new StringBuilder();
        for(int i = 0; i<INTERVALS-1;++i) {
            defSent.append("0, ");   // sentiment values w/ comma delimiters
        }
        defSent.append("0");  // add the last 0 without comma
        AvgProjStr = defSent.toString(); // strings hold list of text
        AvgToneStr = defSent.toString();
        AvgIntonStr = defSent.toString();

        Arrays.fill(SessProj, 0.0f);
        Arrays.fill(SessTone, 0.0f);
        Arrays.fill(SessInton, 0.0f);
    }


    // Method returns a string of Instr State data using comma delimiters
    public String getStrState(){
        String outstr =
                String.valueOf(StringsID)+DELIM+
                        String.valueOf(Brand)+DELIM+
                        String.valueOf(Model)+DELIM+
                        String.valueOf(Type)+DELIM+
                        String.valueOf(Cost)+DELIM+
                        String.valueOf(AvgLife)+DELIM+
                        String.valueOf(ChangeCnt)+DELIM+
                        String.valueOf(FirstSession)+DELIM+
                        String.valueOf(AvgProjStr)+DELIM+
                        String.valueOf(AvgToneStr)+DELIM+
                        String.valueOf(AvgIntonStr);
        return outstr;
    }


    // Method to set Instr State parameters from a string using comma delimiters
    public void setStrState(String line){
        if (line != null) {
            String tokens[] = line.split(DELIM.trim());

            StringsID = Integer.parseInt(tokens[0].trim());
            Brand = tokens[1].trim();
            Model = tokens[2].trim();
            Type = tokens[3].trim();
            Cost = Float.parseFloat(tokens[4].trim());
            AvgLife = Integer.parseInt(tokens[5].trim());
            ChangeCnt = Integer.parseInt(tokens[6].trim());
            FirstSession = Boolean.parseBoolean(tokens[7].trim());
            AvgProjStr = tokens[8].trim();
            AvgToneStr = tokens[9].trim();
            AvgIntonStr = tokens[10].trim();
        }
    }

    // method to copy and convert string lists into float array of avg sentiment values
    void convStr2Float() {
        String ptokens [] = AvgProjStr.split(",");
        String ttokens [] = AvgToneStr.split(",");
        String itokens [] = AvgIntonStr.split(",");
        Log.d("ConvStr2Float:", AvgProjStr+"|"+AvgToneStr+"|"+AvgIntonStr);
        for (int i = 0; i<INTERVALS; ++i) {
            if(i<ptokens.length) {
                AvgProj[i] = Float.valueOf(ptokens[i].trim());
                AvgTone[i] = Float.valueOf(ttokens[i].trim());
                AvgInton[i] = Float.valueOf(itokens[i].trim());
            }
        }
    }


    // method to build strings containing avg sentiment values lists for DB storage
    void convFloat2Str() {
        StringBuilder pstr = null;
        StringBuilder tstr = null;
        StringBuilder istr = null;
        pstr = new StringBuilder();
        tstr = new StringBuilder();
        istr = new StringBuilder();
        // build strings of avg sent values
        for (int i = 0; i<INTERVALS; ++i) {
            String delim;  // delimiter for values in strings
            if(i<INTERVALS-1) {
                delim=", ";
            }
            else {
                delim=""; //null for last value
            }
            pstr.append(Float.toString(AvgProj[i])+delim);
            tstr.append(Float.toString(AvgTone[i])+delim);
            istr.append(Float.toString(AvgInton[i])+delim);
        }
        AvgProjStr = pstr.toString();  // copy to class strings
        AvgToneStr = tstr.toString();
        AvgIntonStr = istr.toString();

    }

    // On a strings change this method takes in a Session SentLog list and normalizes the values to
    // to the 10% of life intervals and does a weighted average of new with existing avgSent.
    // Assumes the strings were already setup previously in config screen
    // This uses a binning method to combine sampled sentiment and interpolates for any empty bins
    void updateAvgSent(List<SessionSent> sentlist, int totaltime) {
        int interval = totaltime/INTERVALS;  // interval for % pieces of lifespan
        int accTime = 0;
        int binindex = 0;
        Arrays.fill(SessProj, 0.0f);  // clear the temp session float arrays
        Arrays.fill(SessTone, 0.0f);
        Arrays.fill(SessInton, 0.0f);


        convStr2Float();

        SessionSent s = sentlist.get(0);

        int [] sessSumT = new int[INTERVALS];
        int [] sessCntT = new int[INTERVALS];

        // accumulate sent values from log list
        for(int i = 0; i < sentlist.size(); ++i){   // how to adapt this as a function to handle al 3 proj,tone,inton
            s = sentlist.get(i);

            if(accTime/interval < 10) {
                binindex = accTime/interval;
            }

            accTime += s.getSessTime();

            sessSumT[binindex] += s.getSessTime();

            SessProj[binindex] += s.getProj();
            SessTone[binindex] += s.getTone();
            SessInton[binindex] += s.getInton();
            sessCntT[binindex] += 1;
            //System.out.println(i+" binIndex = "+binindex+"  sessCnt="+sessCntT[binindex]+" SessProj[]="+SessProj[binindex]);

        }

        // loop checks for missing sent data in bins and interpolates if possible
        // this only handles single missing items unless at end where it repeats last valid value
        for(int i=0; i<INTERVALS; ++i) {

            if(sessCntT[i] == 0) {      // if data missing in a bin
                if(i == 0) {  // first item missing? this is an error condition!
                    SessProj[i] = 0.0f;
                    SessTone[i] = 0.0f;
                    SessInton[i] = 0.0f;
                } else if(i+1 >= INTERVALS) {  // last item missing copy previous value
                    SessProj[i] = SessProj[i-1];
                    SessTone[i] = SessTone[i-1];
                    SessInton[i] = SessInton[i-1];
                } else {  // missing items between 0 and INTERVALS
                    if(sessCntT[i+1] > 0) {    //interpolate when next bin is not empty
                        // note [i-1] avg already computed and [i+1] has not
                        SessProj[i] = (SessProj[i-1] + SessProj[i+1]/(float)sessCntT[i+1])/2;
                        SessTone[i] = (SessTone[i-1] + SessTone[i+1]/(float)sessCntT[i+1])/2;
                        SessInton[i] = (SessInton[i-1] + SessInton[i+1]/(float)sessCntT[i+1])/2;
                    } else {
                        SessProj[i] = SessProj[i-1];  // so if a bin and next bin are empty just copy previous bin
                        SessTone[i] = SessTone[i-1];
                        SessInton[i] = SessInton[i-1];
                    }
                }
            } else {   // otherwise data not missing so find average sent values
                SessProj[i] /= (float)sessCntT[i];
                SessTone[i] /= (float)sessCntT[i];
                SessInton[i] /= (float)sessCntT[i];
            }
            //System.out.println("sessCntT="+sessCntT[i]+" sent vals: p="+SessProj[i]+" t="+SessTone[i]);
        }

        // Now we can update the string sentiment with weighted averages
        for(int j=0; j<INTERVALS; ++j) {
            if(FirstSession) {                 // first update just copy values
                AvgProj[j] = SessProj[j];
                AvgTone[j] = SessTone[j];
                AvgInton[j] = SessInton[j];

            } else {                           // do a weighted average
                AvgProj[j] = ((float)ChangeCnt*AvgProj[j] + SessProj[j])/(float)(ChangeCnt+1);
                AvgTone[j] = ((float)ChangeCnt*AvgTone[j] + SessTone[j])/(float)(ChangeCnt+1);
                AvgInton[j] = ((float)ChangeCnt*AvgInton[j] + SessInton[j])/(float)(ChangeCnt+1);
            }

        }//System.out.println(interval+" i = "+i+" binCnt = "+binindex+" SessProj[]="+SessProj[binindex]+"  sessCnt[]="+sessCntT[binindex]);

        updateAvgLife(totaltime); // update avg life
        ++ChangeCnt;   //increment upon update = strings change event
        convFloat2Str();
        FirstSession = false;

    }

    // method to update AvgLife value in string set.
    void updateAvgLife(int currlife) {
        if(ChangeCnt == 0) {
            AvgLife = currlife;
        } else {     // do a weighted avg
            AvgLife = (AvgLife*ChangeCnt + currlife)/(ChangeCnt+1);
        }
    }

    // getters
    public int getStringsID() {
        return StringsID;
    }
    public String getBrand() {
        return Brand;
    }
    public String getModel() {
        return Model;
    }
    public String getTension() {
        return Tension;
    }
    public String getType() {
        return Type;
    }
    public float getCost() {
        return Cost;
    }
    public int getAvgLife() {
        return AvgLife;
    }
    public int getChangeCnt() {
        return ChangeCnt;
    }
    public boolean getFirstSession() {
        return FirstSession;
    }
    // returns database strings
    public String getAvgProjStr() {
        return AvgProjStr;
    }
    public String getAvgToneStr() {
        return AvgToneStr;
    }
    public String getAvgIntonStr() {
        return AvgIntonStr;
    }
    // returns float arrays
    public float [] getAvgProj() {
        convStr2Float();  // be sure the float arrays are populated
        return AvgProj;
    }
    public float [] getAvgTone() {
        convStr2Float();  // be sure the float arrays are populated
        return AvgTone;
    }
    public float [] getAvgInton() {
        convStr2Float();  // be sure the float arrays are populated
        return AvgInton;
    }

    // setters
    public void setStringsID(int id) {
        StringsID = id;
    }
    public void setBrand(String br) {
        Brand = br;
    }
    public void setModel(String mod) {
        Model = mod;
    }
    public void setTension(String ten) {
        Tension =  ten;
    }
    public void setType(String ty) {
        Type =  ty;
    }
    public void setCost(float cost) {
        Cost =  cost;
    }
    public void setAvgLife(int al) {
        AvgLife = al;
    }
    public void setChangeCnt(int ccnt) {
        ChangeCnt = ccnt;
    }
    public void setFirstSession(boolean fs) {
        FirstSession = fs;
    }

    // sets database strings
    public void setAvgProjStr(String aps) {
        AvgProjStr = aps;
    }
    public void setAvgToneStr(String ats) {
        AvgToneStr = ats;
    }
    public void setAvgIntonStr(String ais) {
        AvgIntonStr = ais;
    }
    // sets float arraysString
    public void setAvgProj(float [] ap) {
        AvgProj = ap;
        convFloat2Str();  // be sure the strings are updated with float arrays
    }
    public void setAvgTone(float [] at) {
        AvgTone = at;
        convFloat2Str();  // be sure the strings are updated with float arrays

    }
    public void setAvgInton(float [] ai) {
        AvgInton = ai;
        convFloat2Str();  // be sure the strings are updated with float arrays
    }


}
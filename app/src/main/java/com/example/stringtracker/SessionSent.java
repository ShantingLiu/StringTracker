package com.example.stringtracker;

// Data class to store user session sentiment input  WKD 3-12-21
public class SessionSent {
    private String timeStamp;  // date and time of session start
    private int sessTime;      // time played in minutes
    private float proj;        // stores user sent feedback for session
    private float tone;
    private float inton;


    // Constructor
    public SessionSent(String ts, int st, float pr, float to, float in){
        timeStamp = ts;
        sessTime = st;
        proj = pr;
        tone = to;
        inton = in;
    };
    // default constructor values
    public SessionSent(){
        timeStamp = "";
        sessTime = 0;
        proj = 0.0f;
        tone = 0.0f;
        inton = 0.0f;
    };

    //Setters
    void setTimeStamp(String ts) {
        timeStamp = ts;
    };
    void setSessTime(int st) {
        sessTime = st;
    };
    void setProj(float pr) {
        proj = pr;
    };
    void setTone(float to) {
        tone = to;
    };
    void setInton(float in) {
        inton = in;
    };

    // Getters
    String getTimeStamp() {
        return timeStamp;
    };
    int getSessTime() {
        return sessTime;
    };
    float getProj() {
        return proj;
    };
    float getTone() {
        return tone;
    };
    float getInton() {
        return inton;
    };

}

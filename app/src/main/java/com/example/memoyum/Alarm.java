package com.example.memoyum;

public class Alarm {
    int _id;
    String name;
    String date;
    int memoId;

    public void setDate(int year, int month, int dayOfMonth) {
        this.date= year +"."+ month +"."+ dayOfMonth;
    }
    public void setMemoId(int mId){
        this.memoId = mId;
    }
}

package com.example.sinupsample;


public class MemoData {
    public String _uid,_memoTitle,_memoContents,_date;
    public MemoData(String uid, String date, String memoTitle, String memoContents){
        this._uid = uid;
        this._date = date;
        this._memoTitle = memoTitle;
        this._memoContents = memoContents;
    }
    public MemoData(){}
    public String get_uid() {
        return _uid;
    }
    public String get_date() {
        return _date;
    }
    public String get_memoTitle() {
        return _memoTitle;
    }
    public String get_memoContents() {
        return _memoContents;
    }

}